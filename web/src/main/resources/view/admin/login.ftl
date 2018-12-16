<!DOCTYPE html>
<html>
<base href="${basePath}"/>
<head>
    <title>${website.title} - ${_res.login}</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="shortcut icon" href="${basePath}favicon.ico"/>
    <link rel="stylesheet" href="${basePath}assets/css/pnotify.css"/>
    <link rel="stylesheet" type="text/css" href="${basePath}assets/css/login.css"/>
    <script src="${basePath}assets/js/jquery.min.js"></script>
    <script src="${basePath}assets/js/pnotify.js"></script>
    <script src="${basePath}admin/js/common.js"></script>
    <script src="${basePath}admin/js/login.js"></script>
</head>
<body>
<div class="limiter">
    <div class="container-login95">
        <div class="wrap-login100">
            <div class="login100-form-title"
                 style="background-image: url('${basePath}assets/images/login-bg.jpg');">
					<span class="login100-form-title-1">
                    ${_res.userNameAndPassword}
                    </span>
            </div>
            <form class="login100-form validate-form" id="login_form" action="${basePath}api/admin/login">
                <input type="hidden" id="redirectFrom" value="${redirectFrom!''}">
                <div class="wrap-input100 validate-input m-b-26">
                    <span class="label-input100">${_res.userName}</span>
                    <input class="input100" type="text" name="userName" value="${userName!''}" id="userName"
                           placeholder="${_res.userName}">
                    <span class="focus-input100"></span>
                </div>

                <div class="wrap-input100 validate-input m-b-18">
                    <span class="label-input100">${_res.password}</span>
                    <input class="input100" type="password" value="${password!''}" name="password"
                           placeholder="${_res.password}">
                    <span class="focus-input100"></span>
                </div>

                <div class="container-login100-form-btn" style="margin-top: 20px">
                    <button id="login_btn" class="login100-form-btn">
                        <?xml version="1.0" standalone="no"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN"
                        "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
                        <svg t="1517682510435" class="icon" style="" viewBox="0 0 1024 1024" version="1.1"
                             xmlns="http://www.w3.org/2000/svg" p-id="1047"
                             width="16" height="16">
                            <defs>
                                <style type="text/css"></style>
                            </defs>
                            <path d="M852.144032 937.162236 285.2368 937.162236c-62.614579 0-113.380832-50.764174-113.380832-113.37721L171.855968 710.408839l56.690416 0 0 113.376187c0 31.306007 25.38415 56.688093 56.690416 56.688093l566.907232 0c31.307289 0 56.690416-25.38311 56.690416-56.688093l0-623.572098c0-31.306007-25.38415-56.688093-56.690416-56.688093L285.2368 143.524834c-31.307289 0-56.690416 25.382087-56.690416 56.688093l0 113.376187-56.690416 0L171.855968 200.212928c0-62.612013 50.766254-113.37721 113.380832-113.37721l566.907232 0c62.615602 0 113.381856 50.765197 113.381856 113.37721l0 623.572098C965.525888 886.398062 914.759634 937.162236 852.144032 937.162236zM606.752315 335.069335c-11.044996-11.072173-11.044996-29.036313 0-40.079834 11.071603-11.072173 29.03648-11.072173 40.109106 0l195.565921 195.557908c5.896532 5.89629 8.414992 13.729699 8.028166 21.451568 0.386826 7.721869-2.131634 15.556301-8.028166 21.48022L646.862445 729.038128c-11.072627 11.07115-29.037503 11.07115-40.109106 0-11.044996-11.072173-11.044996-29.036313 0-40.080857l148.619825-148.613736L86.818808 540.343535c-15.639829 0-28.34572-12.67774-28.34572-28.344558 0-15.639189 12.70589-28.344558 28.34572-28.344558l668.552308 0L606.752315 335.069335z"
                                  p-id="1048" fill="#ffffff"></path>
                        </svg>
                        <span style="padding-left: 5px">${_res.login}</span>
                    </button>
                </div>


            </form>
        </div>
    </div>
    <div class="text-center container-login5">
        <p><strong>${_res['copyrightCurrentYear']}</strong> ${website.title} All Rights Reserved. </p>
    </div>
</div>
</body>
</html>