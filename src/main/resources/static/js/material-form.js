function addQuestionItem() {
    const container = document.getElementById('questions-container');
    const currentIndex = container.querySelectorAll('.question-editor').length;

    const newBox = document.createElement('div');
    newBox.className = 'question-editor';
    newBox.setAttribute('data-index', currentIndex);

    newBox.innerHTML =
        '<div class="form-group">' +
            '<label>Question ' + (currentIndex + 1) + ' Text</label>' +
            '<input type="text" name="questions[' + currentIndex + '].questionText" placeholder="Type question core string..." required>' +
        '</div>' +
        '<div class="form-group">' +
            '<label>Options (Check the radio for the correct answer)</label>' +
            '<div class="option-input">' +
                '<input type="radio" name="questions[' + currentIndex + '].correctOptionIndex" value="0" required>' +
                '<input type="text" name="questions[' + currentIndex + '].options[0]" placeholder="Option text..." required>' +
            '</div>' +
            '<div class="option-input">' +
                '<input type="radio" name="questions[' + currentIndex + '].correctOptionIndex" value="1">' +
                '<input type="text" name="questions[' + currentIndex + '].options[1]" placeholder="Option text..." required>' +
            '</div>' +
            '<div class="option-input">' +
                '<input type="radio" name="questions[' + currentIndex + '].correctOptionIndex" value="2">' +
                '<input type="text" name="questions[' + currentIndex + '].options[2]" placeholder="Option text..." required>' +
            '</div>' +
            '<div class="option-input">' +
                '<input type="radio" name="questions[' + currentIndex + '].correctOptionIndex" value="3">' +
                '<input type="text" name="questions[' + currentIndex + '].options[3]" placeholder="Option text..." required>' +
            '</div>' +
        '</div>';

    container.appendChild(newBox);
}