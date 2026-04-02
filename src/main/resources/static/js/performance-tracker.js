/**
 * Performance Tracking System
 * Tracks user input time and sends to backend
 */

class PerformanceTracker {
    constructor() {
        this.startTime = null;
        this.action = null;
        this.userEmail = null;
        this.isTracking = false;
        this.init();
    }

    /**
     * Initialize performance tracking for forms
     */
    init() {
        // Track login form
        this.trackForm('authForm', 'User Login');
        
        // Track signup form
        this.trackForm('signupForm', 'User Registration');
        
        // Track any other forms with performance-tracking class
        document.querySelectorAll('form.performance-tracking').forEach(form => {
            const action = form.dataset.action || 'Form Submission';
            this.trackForm(form.id, action);
        });
    }

    /**
     * Track a specific form
     */
    trackForm(formId, action) {
        const form = document.getElementById(formId);
        if (!form) return;

        // Find first input field to start tracking
        const firstInput = form.querySelector('input[type="text"], input[type="email"], input[type="password"]');
        if (!firstInput) return;

        // Start tracking when user focuses on first input
        firstInput.addEventListener('focus', (e) => {
            this.startTracking(action, form);
        });

        // Stop tracking on form submission
        form.addEventListener('submit', (e) => {
            this.stopTrackingAndSend(form);
        });
    }

    /**
     * Start performance tracking
     */
    startTracking(action, form) {
        if (this.isTracking) return; // Already tracking

        this.action = action;
        this.startTime = Date.now();
        this.isTracking = true;

        console.log(`Started tracking: ${action} at ${this.startTime}`);

        // Try to get user email from form
        const emailInput = form.querySelector('input[type="email"]');
        if (emailInput) {
            this.userEmail = emailInput.value;
        }
    }

    /**
     * Stop tracking and send data to backend
     */
    stopTrackingAndSend(form) {
        if (!this.isTracking || !this.startTime) return;

        const endTime = Date.now();
        const inputTimeMs = endTime - this.startTime;

        console.log(`Stopped tracking: ${this.action} - Time: ${inputTimeMs}ms`);

        // Get current user email if not already set
        if (!this.userEmail) {
            const emailInput = form.querySelector('input[type="email"]');
            if (emailInput) {
                this.userEmail = emailInput.value;
            }
        }

        // If still no email, try to get from localStorage
        if (!this.userEmail) {
            this.userEmail = localStorage.getItem('email') || 'anonymous';
        }

        // Create enhanced performance data with start/end times
        const performanceData = {
            userEmail: this.userEmail,
            action: this.action,
            inputTimeMs: inputTimeMs,
            startTime: this.startTime,
            endTime: endTime,
            details: `User spent ${inputTimeMs}ms filling ${this.action} form (started: ${new Date(this.startTime).toLocaleString()}, ended: ${new Date(endTime).toLocaleString()})`
        };

        // Send to backend
        this.sendPerformanceData(performanceData);

        // Reset tracking
        this.resetTracking();
    }

    /**
     * Send performance data to backend
     */
    async sendPerformanceData(performanceData) {
        try {
            const response = await fetch('/api/auth/performance', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(performanceData)
            });

            if (response.ok) {
                console.log('Performance data sent successfully:', performanceData);
            } else {
                console.error('Failed to send performance data:', response.statusText);
            }
        } catch (error) {
            console.error('Error sending performance data:', error);
        }
    }

    /**
     * Reset tracking state
     */
    resetTracking() {
        this.startTime = null;
        this.action = null;
        this.userEmail = null;
        this.isTracking = false;
    }

    /**
     * Manual tracking for custom actions
     */
    trackCustomAction(action, startTime, userEmail) {
        const endTime = Date.now();
        const inputTimeMs = endTime - startTime;

        const performanceData = {
            userEmail: userEmail || localStorage.getItem('email') || 'anonymous',
            action: action,
            inputTimeMs: inputTimeMs,
            details: `Custom action: ${action} took ${inputTimeMs}ms`
        };

        this.sendPerformanceData(performanceData);
    }
}

/**
 * Initialize performance tracker when DOM is ready
 */
document.addEventListener('DOMContentLoaded', () => {
    window.performanceTracker = new PerformanceTracker();
    
    // Make it available globally for manual tracking
    console.log('Performance tracking initialized');
});

/**
 * Utility function for manual tracking
 */
function trackPerformance(action, userEmail) {
    if (window.performanceTracker) {
        const startTime = Date.now();
        return {
            end: () => {
                window.performanceTracker.trackCustomAction(action, startTime, userEmail);
            }
        };
    }
    return null;
}
