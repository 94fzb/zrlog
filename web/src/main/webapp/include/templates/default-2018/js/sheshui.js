$(function () {
    $body = $('body');
    var $search = $('#searchform'),
        $search_input = $search.find('input');
    $body.on('click', '[href="#search"]', function (event) {
        event.preventDefault();
        // Not visible?
        if (!$search.hasClass('visible')) {
            // Reset form.
            $search[0].reset();
            // Show.
            $search.addClass('visible');
            // Focus input.
            $search_input.focus();
        }
    });

    $search_input.on('keydown', function (event) {
        if (event.keyCode == 27)
            $search_input.blur();
    })
        .on('blur', function () {
            window.setTimeout(function () {
                $search.removeClass('visible');
            }, 100);
        });
});

// Search clear field value.
function OnEnter(field) {
    if (field.value == field.defaultValue) {
        field.value = "";
    }
}

function OnExit(field) {
    if (field.value == "") {
        field.value = field.defaultValue;
    }
}

/*$(document).ready(function() {
  // check where the shoppingcart-div is
  var offset = $('.asidenav').offset();
  $(window).scroll(function () {
    var scrollTop = $(window).scrollTop(); // check the visible top of the browser
    if (offset.top<scrollTop) $('.asidenav').addClass('fixed');
    else $('.asidenav').removeClass('fixed');
   });
 }); */
// ref: http://stackoverflow.com/questions/5273453/using-jquery-to-keep-scrolling-object-within-visible-window

// 展开抽屉栏
var pannelRight = document.getElementById('drawer-pannel'),
    body = document.body;
window.onload = function () {
    expendDrawerPannel.onclick = function () {
        classie.toggle(this, 'active');
        classie.toggle(pannelRight, 'drawer-display');
        classie.toggle(body, 'drawer-expand');
    };
    overLayer.onclick = function () {
        classie.toggle(pannelRight, 'drawer-display');
        classie.toggle(body, 'drawer-expand');
    };
    collapseDrawerPannel.onclick = function () {
        classie.toggle(pannelRight, 'drawer-display');
        classie.toggle(body, 'drawer-expand');
    };
}

$(document).ready(function () {
    $(".video-js").height(427);
    // check where the shoppingcart-div is
    var offset = $('.top').offset();
    $(window).scroll(function () {
        var scrollTop = $(window).scrollTop(); // check the visible top of the browser
        if (offset.top < scrollTop) $('.top').addClass('fadeout');
        else $('.top').removeClass('fadeout');
    });
    $(document).bind('cbox_open', function () {
        $('html').css({overflow: 'hidden'});
    }).bind('cbox_closed', function () {
        $('html').css({overflow: 'auto'});
    });
    //************懒加载***************
    $("article .content img").each(function () {
        $(this).attr("data-original", $(this).attr("src"));
        $(this).removeAttr("src");
    });
    $(function () {
        $("article .content img").lazyload({
            threshold: 200
        });
    });
    $("img").error(function () {
        $(this).hide();
    });
});

