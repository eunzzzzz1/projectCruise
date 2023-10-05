
        $(document).ready(function(){
     $('.mainPassword i').on('click',function(){
     var inputField = $(this).closest('.mainPassword').find('input.password');
         $(inputField).toggleClass('active');
         if($(inputField).hasClass('password active')){
             $(this).attr('class',"fa-regular fa-eye")
             inputField.attr('type',"text");
         }else{
             $(this).attr('class',"fa-regular fa-eye-slash")
             inputField.attr('type','password');
         }
     });
 });