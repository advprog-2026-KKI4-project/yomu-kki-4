let currentStep = 0;
let steps = [];
let prevBtn = null;
let nextBtn = null;
let submitBtn = null;

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
    if (direction === 1) {
        const currentCard = steps[currentStep];
        const selected = currentCard.querySelector('input[type="radio"]:checked');
        if (!selected) {
            const tempSubmit = document.createElement('input');
            tempSubmit.type = 'submit';
            tempSubmit.style.display = 'none';
            document.getElementById('quiz-form').appendChild(tempSubmit);
            tempSubmit.click();
            tempSubmit.remove();
            return;
        }
    }

    if (currentStep + direction < 0 || currentStep + direction >= steps.length) return;

    steps[currentStep].classList.remove('active');
    currentStep += direction;
    steps[currentStep].classList.add('active');
    updateNavigation();
}

document.addEventListener('DOMContentLoaded', () => {
    steps = document.querySelectorAll('.question-step');
    prevBtn = document.getElementById('prev-btn');
    nextBtn = document.getElementById('next-btn');
    submitBtn = document.getElementById('submit-btn');

    if (prevBtn) prevBtn.addEventListener('click', () => changeQuestion(-1));
    if (nextBtn) nextBtn.addEventListener('click', () => changeQuestion(1));

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