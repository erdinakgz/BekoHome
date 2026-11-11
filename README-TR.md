# BekoHome v0.1.0 — Android TV / Google TV

Beko Google UHD TV için hafif, kumanda ile kullanılabilen **test amaçlı ana ekran uygulaması**.

**Durum:** Alpha / kaynak kodu. İlk fiziksel TV testi ve başarılı APK derlemesi henüz doğrulanmadı.
Bu proje Beko veya Google'ın resmî ürünü değildir.

## Doğrulanmış hedef cihaz
- Android TV OS 14, API 34
- ARM 32-bit (`armeabi-v7a`); uygulamada native kütüphane yok
- Yaklaşık 1,74 GiB toplam RAM
- 4K panel, 1080p arayüz çözünürlüğü
- TV'nin IP adresi ve ADB portu değişebilir

## Özellikler
- Kumanda yön tuşları / OK ile uygulamaları seçme
- Yüklü TV uygulamalarını listeleme, simgelerini gösterme ve çalıştırma
- Canlı Türkçe saat/tarih
- Koyu, minimal arayüz
- Reklam kütüphanesi, takip kodu ve internet izni içermez
- Orijinal Google TV HOME uygulamasını kaldırmaz veya devre dışı bırakmaz

## Windows'a kurulum

1. [Android Studio](https://developer.android.com/studio) yükleyin.
2. GitHub'da **Code → Download ZIP** ile kaynak kodunu indirin veya Git ile klonlayın:

   ```powershell
   git clone https://github.com/erdinakgz/BekoHome.git
   cd BekoHome
   ```

3. Gradle 8.9 Wrapper JAR'ını **SHA-256 doğrulamasıyla** indirin:

   ```powershell
   powershell -ExecutionPolicy Bypass -File .\SETUP-WINDOWS.ps1
   ```

4. Proje klasörünü Android Studio'da açın. JDK 17 ve Android SDK 34 seçili olmalı.
5. **Build → Build APK(s)** ile derleyin veya:

   ```powershell
   .\gradlew.bat assembleDebug
   ```

6. Üretilen APK: `app\build\outputs\apk\debug\app-debug.apk`

## TV bağlantısı / APK kurulumu

TV'de **Geliştirici seçenekleri → Kablosuz hata ayıklama** etkin olmalı.
Bilgisayar TV ile aynı yerel ağda olmalı. Gerekirse TV'de gösterilen eşleştirme
kodu ile `adb pair IP:ESLESTIRME_PORTU` komutunu kullanın.

```powershell
cd C:\platform-tools
.\adb.exe devices -l
# Bağlı değilse, TV'nin kablosuz hata ayıklama ana ekranındaki güncel IP ve bağlantı portu:
.\adb.exe connect TV_IP:ADB_BAGLANTI_PORTU
.\adb.exe devices -l
.\adb.exe install -r "C:\KAYNAK\BekoHome\app\build\outputs\apk\debug\app-debug.apk"
.\adb.exe shell am start -n com.erdinc.bekohome/.MainActivity
```

`adb devices` sonucunda TV `device` durumunda olmalı. Eşleştirme portu ile
bağlantı portu farklıdır. ADB kodlarını herkese açık paylaşmayın.

## TV'de kullanım / geri alma
- Sağ/sol ile uygulama seç, OK ile aç, Geri ile çık.
- HOME tuşu mevcut Google TV ana ekranını açmaya devam eder.
- Uygulamayı kaldırmak için:

  ```powershell
  .\adb.exe uninstall com.erdinc.bekohome
  ```

## Android derlemesi GitHub üzerinden
**Actions → Android debug build** iş akışının başarılı çalışması durumunda
**Artifacts** bölümünden `BekoHome-debug-apk` dosyasını indirebilirsiniz.

## Bilinmesi gerekenler
- APK henüz fiziksel cihazda doğrulanmış değildir.
- Bazı üretici uygulamaları Android paket görünürlüğü kısıtlamaları nedeniyle listede görünmeyebilir.
- HDMI kaynağına doğrudan geçiş bu ilk sürümde yoktur.
- Lisans henüz seçilmedi; resmî bir açık kaynak lisansı eklenmeden yeniden kullanım izni verilmiş sayılmaz.
