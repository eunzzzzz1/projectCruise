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