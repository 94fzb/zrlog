$(function(){
    var baseUrl = $("base").attr("href");
    $("#userName").focus();

    /*function getFormData($form){
        var unindexed_array = $form.serializeArray();
        var indexed_array = {};

        $.map(unindexed_array, function(n, i){
            indexed_array[n['name']] = n['value'];
        });

        return indexed_array;
    }

    function postJson(url,data,error){
        if(!error){
            error = function(e){
                alert(e.responseJSON.message);
            }
        }
        var post =  $.ajax({
            type: "POST",
            url:  url,
            data: JSON.stringify(data),
            contentType: "application/json",
            async: !1
        }).fail(error);
        var status = post.status;
        if(status>=200 && status<300){
            return post.responseJSON;
        }
    }*/

    function login(){
        var url = $("#login_form").attr("action");
        $.post(url,$('#login_form').serialize(),function(e){
            if(!e.error){
                var redirectTo = "";
                if($("#redirectFrom").val().length!=0){
                    redirectTo = $("#redirectFrom").val();
                }else{
                    var baseUrl = $("base").attr("href");
                    redirectTo = baseUrl + "admin/index";
                }
                location.href = redirectTo;

            }else{
                new PNotify({
                  title: e.message,
                  type: 'error',
                  delay:3000,
                  hide: true,
                  styling: 'bootstrap3'
              });
            }
        })
        return false;
    }

    $("#login_btn").click(function(e){
        login();
    });
    $("body").keypress(function(e){
        if(e.which == 13){
            login();
        }
    });

})
