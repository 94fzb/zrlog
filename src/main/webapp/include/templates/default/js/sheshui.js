$(document).ready(function(){
    new gnMenu(document.getElementById( 'gn-menu' ));
    var tags_a = $("#tags").find("a");
    tags_a.each(function(){
        var x = 6;
        var y = 0;
        var rand = parseInt(Math.random() * (x - y + 1) + y);
        $(this).addClass("size"+rand);
    });
});