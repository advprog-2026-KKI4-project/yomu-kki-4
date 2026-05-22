function addQuestionItem() {
    const container = document.getElementById('questions-container');
    const currentIndex = container.querySelectorAll('.question-box').length;

    const newBox = document.createElement('div');
    newBox.className = 'question-box';
    newBox.setAttribute('data-index', currentIndex);
    newBox.style.cssText = "background: #faf6fa; padding: 30px; border-radius: 20px; border: 1px dashed #a096e0; margin-bottom: 25px;";

    newBox.innerHTML =
        '<div style="margin-bottom: 25px;">' +
            '<label style="display: block; font-weight: 700; color: #111016; margin-bottom: 10px; font-size: 14px;">Question ' + (currentIndex + 1) + ' Text</label>' +
            '<input type="text" name="questions[' + currentIndex + '].questionText" placeholder="Type question core string..." style="width: 100%; padding: 14px; border: 1px solid #e0d5dd; border-radius: 10px; font-size: 15px; outline: none;" required>' +
        '</div>' +
        '<div>' +
            '<label style="display: block; font-weight: 700; color: #111016; margin-bottom: 12px; font-size: 14px;">Option Slots Matrix</label>' +
            '<div style="display: flex; align-items: center; gap: 15px; margin-bottom: 12px;">' +
                '<input type="radio" name="questions[' + currentIndex + '].correctOptionIndex" value="0" style="transform: scale(1.3); accent-color: #6b5ce0;" required>' +
                '<input type="text" name="questions[' + currentIndex + '].options[0]" placeholder="Option string payload..." style="flex: 1; padding: 12px; border: 1px solid #e0d5dd; border-radius: 10px; font-size: 14px; outline: none;" required>' +
            '</div>' +
            '<div style="display: flex; align-items: center; gap: 15px; margin-bottom: 12px;">' +
                '<input type="radio" name="questions[' + currentIndex + '].correctOptionIndex" value="1" style="transform: scale(1.3); accent-color: #6b5ce0;">' +
                '<input type="text" name="questions[' + currentIndex + '].options[1]" placeholder="Option string payload..." style="flex: 1; padding: 12px; border: 1px solid #e0d5dd; border-radius: 10px; font-size: 14px; outline: none;" required>' +
            '</div>' +
            '<div style="display: flex; align-items: center; gap: 15px; margin-bottom: 12px;">' +
                '<input type="radio" name="questions[' + currentIndex + '].correctOptionIndex" value="2" style="transform: scale(1.3); accent-color: #6b5ce0;">' +
                '<input type="text" name="questions[' + currentIndex + '].options[2]" placeholder="Option string payload..." style="flex: 1; padding: 12px; border: 1px solid #e0d5dd; border-radius: 10px; font-size: 14px; outline: none;" required>' +
            '</div>' +
            '<div style="display: flex; align-items: center; gap: 15px; margin-bottom: 12px;">' +
                '<input type="radio" name="questions[' + currentIndex + '].correctOptionIndex" value="3" style="transform: scale(1.3); accent-color: #6b5ce0;">' +
                '<input type="text" name="questions[' + currentIndex + '].options[3]" placeholder="Option string payload..." style="flex: 1; padding: 12px; border: 1px solid #e0d5dd; border-radius: 10px; font-size: 14px; outline: none;" required>' +
            '</div>' +
        '</div>';

    container.appendChild(newBox);
}