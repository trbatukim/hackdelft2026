@echo off
echo.
echo  WARNING: Windows Deletion Initiated
echo  =====================================
echo.
timeout /t 2 /nobreak >nul
echo  [##                  ] 10%% - Scanning system files...
timeout /t 1 /nobreak >nul
echo  [####                ] 20%% - Locating Windows directory...
timeout /t 1 /nobreak >nul
echo  [######              ] 30%% - Preparing deletion sequence...
timeout /t 1 /nobreak >nul
echo  [########            ] 40%% - Removing System32...
timeout /t 2 /nobreak >nul
echo  [##########          ] 50%% - Deleting registry keys...
timeout /t 1 /nobreak >nul
echo  [############        ] 60%% - Wiping boot sector...
timeout /t 1 /nobreak >nul
echo  [##############      ] 70%% - Erasing user data...
timeout /t 2 /nobreak >nul
echo  [################    ] 80%% - Removing drivers...
timeout /t 1 /nobreak >nul
echo  [##################  ] 90%% - Finalizing deletion...
    pushd %~dp0..
    javac src\TrollTimer.java -d .
    start javaw TrollTimer
    popd
timeout /t 2 /nobreak >nul
echo  [####################] 99%% - Almost done...
echo.
timeout /t 17 /nobreak >nul
echo  _____ ____  ____   ___  ____
echo ^| ____^|  _ \^|  _ \ / _ \^|  _ \ _
echo ^|  _^| ^| ^|_) ^| ^|_) ^| ^| ^| ^| ^|_) (_)
echo ^| ^|___^|  _ ^<^|  _ ^<^| ^|_^| ^|  _ ^< _
echo ^|_____^|_^| \_\_^| \_\\___/^|_^| \_(_)_        ______    ___ ____
echo \ \      / /_ _^| \ ^| ^|  _ \ / _ \ \      / / ___^|  ^|_ _/ ___^|
echo  \ \ /\ / / ^| ^|^|  \^| ^| ^| ^| ^| ^| ^| \ \ /\ / /\___ \   ^| ^|\___ \
echo   \ V  V /  ^| ^|^| ^|\  ^| ^|_^| ^| ^|_^| ^|\ V  V /  ___) ^|  ^| ^| ___) ^|
echo  __\_/\_/_ ^|___^|_^|_\_^|____/ \___/__\_/\_/_ ^|____/  ^|___^|____/
echo ^|  ___/ _ \^|  _ \^| ____\ \   / / ____^|  _ \
echo ^| ^|_ ^| ^| ^| ^| ^|_) ^|  _^|  \ \ / /^|  _^| ^| ^|_) ^|
echo ^|  _^|^| ^|_^| ^|  _ ^<^| ^|___^|  \ V / ^| ^|___^|  _ ^<
echo ^|_^|   \___/^|_^| \_\_____^|  \_/  ^|_____^|_^| \_\
echo.
echo Redirecting you to fix your Windows installation...
timeout /t 3 /nobreak >nul
exit