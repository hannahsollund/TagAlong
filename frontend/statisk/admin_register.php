<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <link rel="stylesheet" type="text/css" href="css/login.css">

    <title></title>
</head>
<body>


    <div id="loginWrap">
        <div id="loginInner">
            <img id="logo" src="images/tagalong_logo.png" alt="">

            <form action="index.php">
                <h3>Register users</h3>
                <input type="text" name="roles" id="roles" hidden value=",READ_COMMENT,NEW_COMMENT,READ_FEED,NEW_FEED,EDIT_FEED,READ_LINK,NEW_LINK,READ_PAGE,NEW_PAGE,EDIT_PAGE,READ_POST,NEW_POST,EDIT_POST,NEW_STUDYFIELD,READ_STUDYFIELD,READ_UPLOAD,NEW_UPLOAD,READ_ALL_FILES,READ_FILE,READ_SELF,READ_USER,READ_ALL_USERS,READ_USER,NEW_USER,DELETE_USER">

                <input id="username" type="text" placeholder="Username">
                <input id="password" type="password" placeholder="Password">
                <input type="radio" class="radio" name="sex" value="male" id="male" />
                <label for="male">Male</label><br>
                <input type="radio" class="radio" name="sex" value="female" id="female" />
                <label for="female">Female</label><br>

                <input type="submit" value="Register user">
            </form>

        </div>
    </div>

</body>
</html>