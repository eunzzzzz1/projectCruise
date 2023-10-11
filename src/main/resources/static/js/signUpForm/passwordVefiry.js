 function validatePassword() {
                    const passwordInput = document.getElementById("password");
                    const password = passwordInput.value;
                    const passwordError = document.getElementById("password-error");

                    // 비밀번호 유효성 검사 정규표현식
                    const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/;

                    if (passwordPattern.test(password)) {
                        passwordError.textContent = ""; // 유효한 비밀번호
                    } else {
                        passwordError.textContent = "비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자를 사용해야 합니다.";
                    }
                }