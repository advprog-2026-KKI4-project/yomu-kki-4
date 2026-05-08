document.addEventListener('DOMContentLoaded', function() {
    const timerDisplay = document.getElementById('timer-display');
    const durationInput = document.getElementById('duration-input');
    const quizForm = document.getElementById('quiz-form');
    const timeLimitInput = document.getElementById('time-limit');

    if (!timerDisplay || !timeLimitInput || !quizForm || !durationInput) return;

    let timeRemaining = parseInt(timeLimitInput.value);
    const totalTime = timeRemaining;

    const timer = setInterval(function() {
        timeRemaining--;

        let minutes = Math.floor(timeRemaining / 60);
        let seconds = timeRemaining % 60;
        timerDisplay.innerText = `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;

        durationInput.value = totalTime - timeRemaining;

        if (timeRemaining < 10) {
            timerDisplay.style.color = "#d32f2f";
            timerDisplay.classList.add('pulse-animation');
        } else {
            timerDisplay.style.color = "#333";
            timerDisplay.classList.remove('pulse-animation');
        }

        if (timeRemaining <= 0) {
            clearInterval(timer);
            quizForm.submit();
        }
    }, 1000);
});