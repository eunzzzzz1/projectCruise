 document.addEventListener('DOMContentLoaded', function () {
            const phoneNumberInput = document.getElementById('phone-number-input');
            const errorMessage = document.getElementById('phone-number-error');

            phoneNumberInput.addEventListener('input', validatePhoneNumber);

            function validatePhoneNumber() {
                const phoneNumber = phoneNumberInput.value;
                const isNumeric = /^[0-9]+$/.test(phoneNumber);

                if (!isNumeric || phoneNumber.length != 11 ) {
                    // 유효하지 않은 경우 오류 메시지를 표시합니다.
                    errorMessage.textContent = '•유효하지 않은 핸드폰 번호입니다.';


                } else {
                    // 유효한 경우 오류 메시지를 지우고 입력란 테두리를 원래대로 돌립니다.
                    errorMessage.textContent = '';

                }
            }
        });


        function showAlert() {
                alert("회원가입이 완료되었습니다.");
            }