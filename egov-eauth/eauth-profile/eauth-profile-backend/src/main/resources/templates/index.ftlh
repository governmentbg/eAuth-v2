<!DOCTYPE html>
<html lang="uk">
<head>
    <title>Spring Security - SAML 2 Log In</title>
    <meta charset="UTF-8">
</head>
<script>
	// set timeout of 3s before call
	function autoClick() {
  		document.getElementById("profileId").click(); // Click on the link
	}
</script>
<body onload="setTimeout(autoClick, 3000)">
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
	<!-- localhost: http://eauth.egov.bg:4201/register -->
	<a id="profileId" href="https://eauth.egov.bg/profile/register">Профил в двуфакторната автентикация</a>
	<ul>
		<li>
			<a href="/logout">Log Out</a>
		</li>
		<!--
		<li>
			 <a href="http://localhost:8080/logout">Log Out SSO</a> 
		</li>
		-->
	</ul>
</body>
</html>