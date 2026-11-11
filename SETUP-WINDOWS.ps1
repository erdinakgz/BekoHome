# Gradle 8.9 Wrapper JAR: Gradle'in resmi checksum listesi ile SHA-256 dogrulama
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$dest = Join-Path $root 'gradle\wrapper\gradle-wrapper.jar'
$url = 'https://raw.githubusercontent.com/gradle/gradle/v8.9.0/gradle/wrapper/gradle-wrapper.jar'
$expected = '498495120a03b9a6ab5d155f5de3c8f0d986a449153702fb80fc80e134484f17'
if (-not (Test-Path $dest)) {
  Write-Host 'Resmi Gradle 8.9 Wrapper JAR indiriliyor...'
  Invoke-WebRequest -Uri $url -OutFile $dest -UseBasicParsing
}
$actual = (Get-FileHash -Path $dest -Algorithm SHA256).Hash.ToLowerInvariant()
if ($actual -ne $expected) {
  Remove-Item $dest -Force
  throw 'Gradle Wrapper SHA-256 dogrulamasi basarisiz, dosya silindi.'
}
Write-Host 'Gradle Wrapper dogrulandi.' -ForegroundColor Green
