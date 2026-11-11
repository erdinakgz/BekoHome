# BekoHome v0.2.0 — Fiziksel TV test planı

Hedef: Beko Google UHD TV, Android TV OS 14 (API 34), kablosuz ADB.

## Hazırlık

- GitHub Actions'tan başarılı derlemeye ait **app-debug.apk** indir.
- `adb devices -l` içinde TV `device` olsun.
- Aynı debug anahtarından üretilmiş APK'da `adb install -r` kullan.
- İmza değiştiyse, önce `adb uninstall com.erdinc.bekohome`, sonra yeniden yükle. **Bu işlem BekoHome'un yerel düzen verilerini sıfırlar.**

## Kontrol listesi

- [ ] 1. Açılışta sade koyu ekran, saat, tarih ve kategori butonları tam görünüyor
- [ ] 2. `Tümü` sekmesinde TV uygulamaları listeleniyor
- [ ] 3. D-pad sol/sağ ile uygulamalar, yukarı ile kategoriler seçilebiliyor
- [ ] 4. OK ile uygulama açılıyor; Geri ile launcher'a dönüyor
- [ ] 5. Bir uygulamada **OK'ye uzun basınca** düzenleme menüsü açılıyor
- [ ] 6. Alternatif **✎ Düzenle** butonu kumandayla çalışıyor
- [ ] 7. Uygulama `Film & Dizi` kategorisinden `Diğer` kategorisine taşınabiliyor
- [ ] 8. `Tümü` sekmesinde uygulama kaybolmuyor
- [ ] 9. Sola/sağa taşı ile kart sırası değişiyor
- [ ] 10. Favoriye ekleyince sarı yıldız görünüyor; Favoriler sekmesinde listeleniyor
- [ ] 11. Favoriden çıkarılınca yıldız kayboluyor
- [ ] 12. `Son Açılanlar` yalnızca BekoHome üzerinden başlatılan uygulamaları gösteriyor
- [ ] 13. Uygulama kapatılıp tekrar açıldığında kategori ve sıra korunuyor
- [ ] 14. TV yeniden başlatıldıktan sonra kategori, sıra ve favoriler korunuyor
- [ ] 15. Home tuşu orijinal Google TV ana ekranını açıyor
- [ ] 16. App listesinde uzun süre sağa/sola gezinirken donma veya focus kayması yok

## Sorun giderme

```powershell
cd C:\platform-tools
.\adb.exe devices -l
.\adb.exe shell am start -n com.erdinc.bekohome/.MainActivity
.\adb.exe logcat -d -t 300 -s AndroidRuntime:E ActivityTaskManager:I
```

İstediğimiz test sonucu: ekran taşması yok, uygulama yönetimi kumandayla yapılabiliyor, kayıtlar korunuyor ve sistem launcher'ı güvenle açılıyor.

Uyarı: Bir APK'nın GitHub Actions'ta derlenmesi, fiziksel cihazda hatasız çalıştığının kanıtı değildir.
