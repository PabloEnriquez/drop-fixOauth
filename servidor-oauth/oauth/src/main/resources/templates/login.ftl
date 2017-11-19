<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        
        <meta name="description" content="">
        <meta name="author" content="">
        <title>OAuth 2.0</title>
        <link rel="stylesheet" href="css/wro.css"/>
    </head>
    <body>
        <#if RequestParameters['error']??>
            <div  class="alert alert-danger">
                Hubo un problema al Iniciar Sesión. Por favor inténtelo de nuevo.
            </div>
        </#if>
        <div class="container">
            <form class="form-signin" action="login" method="post">
                <div id="logo"></div><br><br>
                <h2 class="form-signin-heading">OAuth 2.0</h2>
                    <div class="form-group">                        
                        <input type="email" id="username" name="username" class="form-control" placeholder="Correo" required autofocus>
                        <input type="password" id="password" name="password" class="form-control" placeholder="Contraseña" required>
                    </div>
                <button type="submit" class="btn btn-lg btn-primary btn-block">Iniciar Sesión</button>
            </form>
        </div>
        <script src="js/wro.js" type="text/javascript"></script>
    </body>
</html>
