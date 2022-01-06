<!DOCTYPE html>
<html lang="uk">
<head>
    <title>Spring Security - SAML 2 Log In</title>
    <meta charset="UTF-8">
    <link href="main.css" rel="stylesheet"/>
</head>
<script>
	function executeFunctions() {
		doOverlay();
		setTimeout(autoClick, 3000);
	}
	function doOverlay() {
		var overlay = document.getElementById('overlayId');
		overlay.style.visibility='visible';
		overlay.style.opacity = '1'
	}
	// set timeout of 3s before call
	function autoClick() {
  		document.getElementById("profileId").click(); // Click on the link
	}
</script>
<body onload="executeFunctions()">
<!--
	<h1>Success</h1> <br/>
	<div>
		You are authenticated by IdP: 
		<strong><span>${idpName}</span></strong>
	</div>
	<p>
		Name: ${username}
	</p>
	<p>
		Identifier: ${identifier}
	</p>
-->
	<div id="overlayId" class="overlay">
		<div class="waiting-text-container">
    		Пренасочване към профил в двуфакторната автентикация...
    	</div>
    	<div class="waiting-loader-container">
    		<img src="loading.jpg" alt="loading..." width="50" />
    	</div>
  	</div>
  	
	<!-- localhost: http://eauth-test.egov.bg:4201/register -->
	<a id="profileId" href="https://eauth-test.egov.bg/profile/register">Профил в двуфакторната автентикация</a>

<!--
	<ul>
		<li>
			<a href="/logout">Log Out</a>
		</li>
	</ul>
-->
</body>
</html>