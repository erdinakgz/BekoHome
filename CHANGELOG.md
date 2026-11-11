# Changelog

Sürüm politikası: Semantic Versioning (`MAJOR.MINOR.PATCH`). Test edilmemiş işlevler kararlı sürüm olarak duyurulmaz.

## [0.2.0] - 2026-11-12

### Added
- Kategoriler: Favoriler, Son Açılanlar, Film & Dizi, Müzik, Oyun, Araçlar, Diğer, Tümü
- Uygulamaları kategoriler arasında taşıma (uzun OK → Kategori değiştir)
- Aynı kategoride sola/sağa sıralama
- Favorileri yönetme ve kart üzerinde yıldız göstergesi
- Beko Home'dan açılan son 8 uygulamanın yerel geçmişi
- Kategori, favori ve sıralama için `SharedPreferences` kalıcı depolama
- Kumandasında uzun OK olmayanlar için görünür **Düzenle** butonu ve MENU tuşu desteği
- Sürüm için ayrıntılı TV test rehberi

### Changed
- Büyük karşılama kutusunun yerini minimal, kompakt bir ana ekran aldı
- Daha hafif kart geçiş animasyonları ve odak vurgusu
- App metadata ve yerel düzen mantığı ayrı Java sınıflarına taşındı
- `versionCode=3`, `versionName=0.2.0`

### Testing
- v0.2.0 fiziksel cihaz testi **bekliyor**
- v0.1.1 üzerinde uygulama listesi, kumanda sağ/sol, OK ile uygulama başlatma, Geri/Home testleri başarılı

## [0.1.1] - 2026-11-12

### Fixed
- 1080p Google TV arayüzünde alt tarafta kesilen uygulama kartları için kompakt dikey yerleşim
- Uygulama adları iki satıra kadar gösterilir
- Kumanda odağındaki kart için yatay boşluk
- Farklı ekran yüksekliği için boyut kaynakları

### Testing
- Beko Google UHD TV üzerinde uygulama ve kumanda testleri başarılı
- 29 uygulama listelendi, YouTube uygulaması açıldı

## [0.1.0] - 2026-11-11

### Added
- Android TV / Google TV için ilk prototip
- Kumanda ile gezinme ve uygulama açma
- Türkçe saat/tarih ve koyu tema
- Android Studio projesi, Türkçe/İngilizce rehber, GitHub Actions

### Notes
- Google TV sistem launcher'ını değiştirmez
