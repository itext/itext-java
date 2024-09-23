REM create the test keys
IF [%1] == [] goto continue
md keys
openssl genrsa -out keys/root_key.pem -aes256 -passout pass:testpassphrase 2048
openssl genrsa -out keys/im_key.pem -aes256 -passout pass:testpassphrase 2048
openssl genrsa -out keys/im2_key.pem -aes256 -passout pass:testpassphrase 2048
openssl genrsa -out keys/sign-key.pem -aes256 -passout pass:testpassphrase 2048
openssl genrsa -out keys/crl-key.pem -aes256 -passout pass:testpassphrase 2048
:continue

call :runTestCase happyPath
call :runTestCase crlIssuerRevokedBeforeSigningDate
call :runTestCase crlIssuerAndSignCertHaveNoSharedRoot
call :runTestCase crlSignerInValidatedChain
call :runTestCase multipleCrlIssuerCandidates
EXIT

:runTestCase
echo running test case %1
rd /S /Q %1
REM generate certificates
ECHO certomancer --config %1.yml mass-summon default %1 --no-pfx --flat
certomancer --config %1.yml mass-summon default %1 --no-pfx --flat
ECHO type %1\*.pem > %1\chain.x
type %1\*.pem > %1\chain.x
ECHO rename %1\chain.x %1\chain.pem
rename %1\chain.x chain.pem

EXIT /B