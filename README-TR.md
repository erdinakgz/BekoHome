# BekoHome v0.2.0 — Android TV / Google TV

Beko Google UHD TV için geliştirdiğimiz **hafif, reklamsız, özelleştirilebilir ana ekran uygulaması**.

> **Sürüm durumu:** Test sürümü. Önceki v0.1.1 Beko TV üzerinde akıcı çalıştı; sağ/sol, OK ve Geri/Home testleri başarılı. **v0.2.0 henüz gerçek TV üzerinde doğrulanmadı.**
>
> Bu, Beko veya Google'ın resmî uygulaması değildir.

## Yeni tasarım ve özellikler

- Daha sade koyu arayüz; büyük karşılama kutusu kaldırıldı
- Kategoriler: **Favoriler · Son Açılanlar · Film & Dizi · Müzik · Oyun · Araçlar · Diğer · Tümü**
- Her uygulamayı istediğin kategoriye taşıyabilirsin
- Uygulamaları bulunduğu kategoride **sola/sağa kaydırarak sıralayabilirsin**
- Favori uygulamalar köşedeki yıldızla işaretlenir
- Son Açılanlar, sadece Beko Home içinden açtığın uygulamaları gösterir
- Favoriler, kategoriler ve uygulama sıraları **televizyonda yerel olarak kaydedilir**
- İnternet, reklam ve takip izni yok
- Orijinal Google TV ana ekranını kaldırmaz

## Kumandayla kullanım

1. **Sağ/sol:** Uygulamalar ve kategoriler arasında gezin.
2. **OK:** Seçili uygulamayı aç.
3. **Uygulama kartında OK'ye basılı tut:** Uygulama düzenleme menüsünü aç.
4. **Alternatif:** Bir uygulamayı seç, üstteki **✎ Düzenle** butonuna git ve OK bas.
5. **Kategori değiştir:** Film & Dizi / Müzik / Oyun / Araçlar / Diğer arasından seç. Uygulama otomatik olarak hedef kategoride gösterilir.
6. **Favorilere ekle/çıkar:** Düzenleme menüsünden seç.
7. **Sola taşı / Sağa taşı:** Aynı kategori içindeki sırayı değiştir.
8. **Geri / Home:** Normal Google TV arayüzüne dön.

Yeni uygulamalar otomatik önerilen kategorilere yerleşir; dilediğinde kategoriyi değiştirebilirsin. `Tümü` bölümünde her uygulama görünür.

## Hedef TV

- Beko Google UHD TV
- Android TV OS 14 · API 34
- ARM `armeabi-v7a`
- Yaklaşık 1,74 GiB RAM
- 4K panel / 1080p arayüz

## GitHub Actions ile APK

1. <https://github.com/erdinakgz/BekoHome/actions> sayfasını aç.
2. **Android debug build** iş akışına gir.
3. En güncel, **yeşil onaylı** çalışmayı seç.
4. En alttaki **Artifacts → BekoHome-debug-apk** dosyasını indir.
5. ZIP'in içindeki **app-debug.apk** dosyasını çıkar.

GitHub'daki kaynak kodu indirmek için: **Code → Download ZIP**. Bu ZIP, Android Studio projesidir; APK değildir.

## ADB ile test yüklemesi (Windows)

Önce TV'de **Geliştirici seçenekleri → Kablosuz hata ayıklama** açık olmalı. Windows bilgisayar aynı yerel ağda olmalı.

```powershell
cd C:\platform-tools
.\adb.exe devices -l
```

`device` görünüyorsa, indirilen APK'yı örneğin `C:\BekoHome-Test\app-debug.apk` yoluna koyup:

```powershell
.\adb.exe install -r "C:\BekoHome-Test\app-debug.apk"
.\adb.exe shell am start -n com.erdinc.bekohome/.MainActivity
```

**INSTALL_FAILED_UPDATE_INCOMPATIBLE** görürsen yeni APK'nın imzası mevcut APK'yla farklıdır. Sadece BekoHome'u kaldırıp tekrar yükle:

```powershell
.\adb.exe uninstall com.erdinc.bekohome
.\adb.exe install "C:\BekoHome-Test\app-debug.apk"
```

**Uyarı:** Kaldırma işlemi BekoHome'da kayıtlı kategori, favori ve sıralama verilerini siler; Google TV'nin kendi uygulamalarını etkilemez. Aynı imzalı APK'larda `install -r` bu verileri korur.

## Lokal Android Studio derlemesi

```powershell
git clone https://github.com/erdinakgz/BekoHome.git
cd BekoHome
powershell -ExecutionPolicy Bypass -File .\SETUP-WINDOWS.ps1
.\gradlew.bat assembleDebug
```

Çıktı: `app\build\outputs\apk\debug\app-debug.apk`

## v0.2.0 TV testleri

Ayrıntılı kontrol listesi: [docs/TEST-PLAN-v0.2.0.md](docs/TEST-PLAN-v0.2.0.md)

- Kategoriler arasında kumandayla geçebiliyor musun?
- Uzun OK ile düzenleme menüsü açılıyor mu?
- Netflix'i örneğin Film & Dizi'den başka bir kategoriye taşıyabiliyor musun?
- Uygulamayı sola/sağa kaydırdıktan sonra sırası korunuyor mu?
- Bir uygulama favori yapıldığında yıldız simgesi çıkıyor mu?
- Uygulamadan geri dönünce ve TV yeniden açılınca ayarlar korunuyor mu?
- Home tuşuyla normal Google TV'ye dönülebiliyor mu?

## Lisans

Depo herkese açıktır; ancak yeniden kullanım izni veren bir lisans henüz eklenmedi. Katkılara açmadan önce MIT veya Apache-2.0 gibi bir lisans belirlemeliyiz.
