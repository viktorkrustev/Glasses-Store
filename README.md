# 🕶️ NOIR – Android приложение за виртуално пробване и покупка на очила

**NOIR** е модерно мобилно приложение за Android, съчетаващо онлайн пазаруване с **виртуално пробване на очила** чрез **камера и лицево разпознаване**. Проектът използва технологии като **ML Kit**, **CameraX**, **Room Database** и **SharedPreferences**, и предлага пълна потребителска функционалност – от разглеждане на продукти до плащане и доставка.

---

## 📲 Основни функционалности

### 🛍️ Каталог с продукти
- Преглед на налични очила с име, марка, цена, форма и изображение
- Сортиране по **име** и **цена**
- Филтриране по **категория** (мъжки/дамски/детски) и **форма на рамката**

### 🔍 Детайли за продукта
- По-голямо изображение
- Подробности за модела
- **Бутон „Пробвай“** за AR режим
- Бутон за **добавяне на отзиви**

### 🧠 Виртуално пробване (AR)
- Стартиране на **TryOnActivity**
- Използване на **ML Kit Face Detection**
- Предна камера + CameraX
- Разпознаване на очи и лице
- Налагане на изображение на очила спрямо позицията и размера на лицето в реално време

### 🛒 Количка и поръчки
- Добавяне/премахване на продукти
- Преглед на обща сума
- Поръчка с въвеждане на телефон и **автоматично попълване на адрес** чрез GPS

### 👤 Потребителски профил
- Профил с avatar снимка от галерията
- Преглед на направените поръчки
- Запазване на данни чрез **SharedPreferences**

### 📌 За нас
- Страница с информация за магазина
- Интерактивна **карта** с местоположение
- Линкове към **Facebook**, **Instagram** и **Twitter**
- Възможност за **директно набиране на телефон** чрез натискане


## 🧩 Архитектура на проекта

Проектът е структуриран по модела **MVC (Model-View-Controller)**:

| Компонент | Съдържание |
|----------|------------|
| 🧠 Model | `Product.java`, `Order.java`, `User.java` и др., база данни Room (`AppDatabase`, `CartDao`, `ProductDao` и др.) |
| 🎨 View | XML layout-и (`activity_main.xml`, `activity_cart.xml`, `activity_tryon.xml` и др.) и адаптери (`ProductAdapter`, `CartAdapter`) |
| 🎮 Controller | `MainActivity.java`, `CartActivity.java`, `ProfileActivity.java`, `TryOnActivity.java` и др. |


## 🧠 Използвани технологии и библиотеки

| Технология | Описание |
|------------|----------|
| Java | Основен език за разработка |
| Android SDK | Платформа за Android |
| Room | Локална база от данни |
| ML Kit (Face Detection) | Разпознаване на лице и очи |
| CameraX | Достъп до предна камера |
| Glide | Зареждане на изображения от URL и локални ресурси |
| RecyclerView | Динамични списъци от продукти и поръчки |
| SharedPreferences | Запазване на ID, профилна снимка и данни |
| Permissions & Intents | Достъп до камера, галерия, местоположение, набиране |


## 📸 Скриншоти

| Splash Screen | Login | Register |
|---------------|-------|----------|
| ![Splash Screen](https://github.com/user-attachments/assets/0c45da84-f3a2-4f1e-8bbf-ee2cc8b1b54d) | ![Login](https://github.com/user-attachments/assets/cbbf0fc4-63e8-4cde-8d20-db3969cb47a0) | ![Register](https://github.com/user-attachments/assets/de792564-4618-40de-bfd9-56a0e1b35af3) |

