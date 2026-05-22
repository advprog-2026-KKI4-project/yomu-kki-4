let currentStep = 0;
const steps = document.querySelectorAll('.question-step');
const prevBtn = document.getElementById('prev-btn');
const nextBtn = document.getElementById('next-btn');
const submitBtn = document.getElementById('submit-btn');

function updateNavigation() {
    if (!prevBtn || !nextBtn || !submitBtn || steps.length === 0) return;

    prevBtn.style.visibility = currentStep === 0 ? 'hidden' : 'visible';

    if (currentStep === steps.length - 1) {
        nextBtn.style.display = 'none';
        submitBtn.style.display = 'block';
    } else {
        nextBtn.style.display = 'block';
        submitBtn.style.display = 'none';
    }
}

function changeQuestion(direction) {
    if (currentStep + direction < 0 || currentStep + direction >= steps.length) return;

    steps[currentStep].classList.remove('active');
    currentStep += direction;
    steps[currentStep].classList.add('active');
    updateNavigation();
}

document.addEventListener('DOMContentLoaded', () => {
    updateNavigation();

    let lastSeconds = -1;
    setInterval(() => {
        const timerDisplay = document.getElementById('timer-display');
        const timerBox = document.getElementById('timer-box');
        if (timerDisplay && timerBox) {
            const timeParts = timerDisplay.innerText.split(':');
            if (timeParts.length === 2) {
                const totalSeconds = parseInt(timeParts[0]) * 60 + parseInt(timeParts[1]);
                if (totalSeconds !== lastSeconds) {
                    lastSeconds = totalSeconds;
                    if (totalSeconds <= 10 && totalSeconds > 0) {
                        timerBox.classList.remove('timer-critical');
                        void timerBox.offsetWidth;
                        timerBox.classList.add('timer-critical');
                    } else {
                        timerBox.classList.remove('timer-critical');
                    }
                }
            }
        }
    }, 200);
});