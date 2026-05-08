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
        timerDisplay.textContent = `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;

        durationInput.value = totalTime - timeRemaining;

        if (timeRemaining <= 10 && timeRemaining > 0) {
            timerDisplay.parentElement.classList.add('timer-urgent');
        } else {
            timerDisplay.parentElement.classList.remove('timer-urgent');
        }

        if (timeRemaining <= 0) {
            clearInterval(timer);
            durationInput.value = totalTime;
            quizForm.submit();
        }
    }, 1000);
});