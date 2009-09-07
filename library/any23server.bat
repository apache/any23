@echo off
set ANY23_ROOT=%~p0
set CP="%ANY23_ROOT%build"
call :findjars "%ANY23_ROOT%lib"
java -cp %CP% -Xmx256M org.deri.any23.servlet.Any23Server %1 %2 %3 %4 %5 %6 %7 %8 %9
exit /B

:findjars
for %%j in (%1\*.jar) do call :addjar "%%j"
for /D %%d in (%1\*) do call :findjars "%%d"
exit /B

:addjar
set CP=%CP%;%1
