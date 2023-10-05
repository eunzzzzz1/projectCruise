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
