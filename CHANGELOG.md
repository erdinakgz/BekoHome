# Changelog

Bu proje sürümlemeyi Semantic Versioning (`MAJOR.MINOR.PATCH`) ilkeleriyle yürütmeyi hedefler.

## [0.1.1] - 2026-11-12

### Fixed
- 1080p Google TV arayüzünde alt tarafta kesilen uygulama kartları için kompakt dikey yerleşim
- Uygulama adları tek satır yerine en fazla iki satırda gösterilir
- Kumanda odağındaki kartın kenarlarda kesilmemesi için yatay satır boşlukları
- Küçük ve büyük yüksekliklerde farklı ekran ölçüleri (`values/` ve `values-h650dp/`)

### Changed
- Uygulama `versionCode=2`, `versionName=0.1.1`
- Kartlar, başlık ve karşılama alanı küçük ekranlarda daha az yer kaplar
- İlk sürümde çalışan sağ/sol, OK, Geri ve Home davranışları değiştirilmedi

### Test durumu
- Kullanıcı, Beko Google UHD TV (Android TV OS 14) üzerinde **v0.1.0** için sağ/sol, OK, Geri/Home testlerini başarılı bildirdi.
- **v0.1.1 henüz TV üzerinde test edilmedi.** Bu sürümde ekranın altındaki uygulama adları ve yönlendirme metninin tam göründüğü kontrol edilmeli.

## [0.1.0] - 2026-11-11

### Added
- Android TV / Google TV için ilk ana ekran prototipi
- Kumanda ile gezinme ve uygulama başlatma
- Türkçe saat / tarih ve koyu tema
- Android Studio Gradle projesi, güvenli Gradle Wrapper edinme betikleri
- Türkçe ve İngilizce README
- GitHub Actions debug APK derleme iş akışı

### Notes
- İlk sürüm alpha niteliğindeydi; sistem launcher'ını değiştirmez.
