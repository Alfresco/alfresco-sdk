<html>
<head>
    <title>${test?html}</title>
    <script language="JavaScript">
        function showdiv(id) {
            //safe function to show an element with a specified id

            if (document.getElementById) { // DOM3 = IE5, NS6
                document.getElementById(id).style.display = 'block';
            }
            else {
                if (document.layers) { // Netscape 4
                    document.id.display = 'block';
                }
                else { // IE 4
                    document.all.id.style.display = 'block';
                }
            }
        }
    </script>
</head>
<body>
<h1>Integration Test: ${test?html}</h1>
<div><b>Result:</b> ${result}</div>
<#if failures??>
    <#list failures as failure>
    <div id="testHeader"><b>${failure.getTestHeader()?html}</b></div>
    <div id="message"><a href="#" onclick="showdiv('trace');return false;">${failure.getMessage()?html}</a></div>
    <div id="trace" style="display:none;"><pre>${failure.getTrace()?html}</pre></div>
    <br/>
    </#list>
</#if>
</body>
</html>