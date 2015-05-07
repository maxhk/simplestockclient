@echo off

set workdir=%~dp0..
set protoc=E:\Run\protoc.exe

::echo %workdir%

%protoc% --java_out=%workdir%\src market.proto