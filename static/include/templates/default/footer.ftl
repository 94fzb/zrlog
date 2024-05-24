</div>
</div>
</div>
<footer>
    <div class="container">
        <hr>
        <div class="row text-center">
            <div class="col-lg-12 footer-below">
                <p>
                    ${_res.footerLink!""}
                </p>
                <p class="copyright">
                    <span>${_res['copyrightCurrentYear']}</span>
                    <span>${webs.title}</span>
                    <#if webs.icp?has_content><span>${webs.icp}</span></#if>
                </p>
            </div>
            <div class="col-lg-12 footer-below">
                <a href="${baseUrl}">${host}</a>
            </div>
        </div>
    </div>
</footer>
<#if webs.webCm?has_content><div style="display:none">${webs.webCm!""}</div></#if>
</body>
</html>
