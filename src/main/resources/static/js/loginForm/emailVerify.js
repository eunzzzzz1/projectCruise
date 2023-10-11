 $(document).ready(function () {
            $("#emailInput").on("input", function () {


         var emailStatus = $("#emailStatus");
         var emailInput = $("#emailInput");

                // Basic email format validation
                var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;

            emailInput.on("blur",function (event) {

                    var email = emailInput.val();

                if (!emailPattern.test(email)) {
                    // Email is not in a valid format
                    emailStatus.text("이메일 형식에 맞게 써주십시오.");
                    $("#emailStatus").show();
                }

    });
   });
     var hiddenDivVisible = false;
               $("#showHiddenDivButton").click(function () {
                   $.ajax({
                       type: "GET",
                       url: "/showHiddenDiv",
                       success: function (response) {
                           if (hiddenDivVisible) {
                               $("#hiddenDiv").hide();
                               hiddenDivVisible = false;
                           } else {

                               $("#hiddenDiv").show();
                               hiddenDivVisible = true;
                           }
                                   }
                   });
               });
           });
            $("#signupForm").submit(function (event) {
               event.preventDefault();
               this.submit();
           });


function showAlert() {
                alert("로그인이 완료되었습니다.");
            }