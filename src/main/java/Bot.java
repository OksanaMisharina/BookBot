import org.glassfish.grizzly.utils.ArrayUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Bot extends TelegramLongPollingBot {

    private Book books = new Book();
    private String chat_id;
    private String lastMessage = "";
    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

    @Override
    public void onUpdateReceived(Update update) {


        update.getUpdateId();

        chat_id = String.valueOf(update.getMessage().getChatId());
        SendMessage sendMessage = new SendMessage().setChatId(chat_id);

        sendMessage.setReplyMarkup(replyKeyboardMarkup); //станавливаем клавиатуру для пользователя
        sendMessage.setText(getMessage(update.getMessage().getText())); //Отправка сообщений пользователю
        try {

            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private void searchPanel() {
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();

        replyKeyboardMarkup.setResizeKeyboard(true);
        keyboard.clear(); //чистим клавиатуру

        keyboardRow1.add("Поиск по названию");
        keyboardRow1.add("Поиск по автору");
        keyboardRow2.add("Поиск по категориям");

        keyboard.add(keyboardRow1);
        keyboard.add(keyboardRow2);
        replyKeyboardMarkup.setKeyboard(keyboard);
    }


    public String getMessage(String msg) {              //клавиатура

        String[] subj = {
                "ARCHITECTURE", "ART", "BIOGRAPHY & AUTOBIOGRAPHY", "BUSINESS & ECONOMICS",
                "COMICS & GRAPHIC NOVELS", "COMPUTERS", "COOKING", "CRAFTS & HOBBIES", "DESIGN", "DRAMA",
                "EDUCATION", "FICTION", "FOREIGN LANGUAGE STUDY", "HISTORY", "LAW",
                "MATHEMATICS", "MEDICAL", "MUSIC", "PHILOSOPHY", "PHOTOGRAPHY", "POETRY", "SCIENCE"};
        String[] subjRu = {
                "Архитектура", "Искусство", "Биография и автобиография", "Бизнес и экономика",
                "Комиксы и графические романы", "Компьютеры", "Готовка", "Хобби", "Дизайн", "Драма",
                "Образование", "Художественная литература", "Иностранные языки",  "История","Закон",
                "Математика", "Медицина", "Музыка", "Философия", "Фотография", "Поэзия", "Наука"};

        System.out.println("-------------------------------------------");
        System.out.println(msg);
        switch (lastMessage.toLowerCase()) {
            case "по названию":
            case "по автору":{
                ArrayList<String> bookList = books.searchBook(lastMessage, msg);
                lastMessage = "";
                searchPanel();
                return getBooksInfo(bookList);
            }
            case "по категориям": {
                int subIndex = ArrayUtils.indexOf(subjRu, msg);
                ArrayList<String> bookList = books.searchBook(lastMessage, subj[subIndex]);
                lastMessage = "";
                searchPanel();
                return getBooksInfo(bookList);
            }
        }

        switch (msg.toLowerCase()) {
            case "привет": {
                String[] answers = {"Приветствую вас", "Мое почтение", "Приветствую", "Здравствуйте", "Бонжур", "Хеллоу"};

                SendMessage sendMessage = new SendMessage().setChatId(chat_id);
                sendMessage.setText(answers[new Random().nextInt(answers.length)]); // возвращает случайный элемент массива
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                return ("Чтобы начать поиск введите команду \"поиск\"");// возвращает случайный элемент массива
            }
            case "поиск": {

                searchPanel();
                return "Выберите тип поиска 🍀";
            }
            case "поиск по названию": {
                lastMessage = "по названию";
                return "Введите название книги";
            }
            case "поиск по автору": {
                lastMessage = "по автору";
                return "Введите имя автора";
            }
            case "поиск по категориям": {

                ArrayList<KeyboardRow> keyboard = new ArrayList<>();

                replyKeyboardMarkup.setResizeKeyboard(true);
                try {

                    for (int i = 1; i <= 11; i++) {
                        KeyboardRow keyboardRow = new KeyboardRow();
                        keyboardRow.add(subjRu[i * 2 - 2]);
                        keyboardRow.add(subjRu[i * 2 - 1]);

                        keyboard.add(keyboardRow);
                        replyKeyboardMarkup.setKeyboard(keyboard);
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                }

                lastMessage = "по категориям";
                return "Выберите категорию";
            }
            default:
                lastMessage = "";
                return "Я не понимаю";
        }

    }

        public String getBooksInfo (ArrayList < String > bookList) {

            if (bookList.isEmpty())
                return "По данному запросу ничего не найдено";

            SendMessage sendMessage = new SendMessage().setChatId(chat_id);
            for (String book : bookList) {
                try {
                    sendMessage.setText(book);
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            return "\uD83D\uDC40";
        }

        @Override
        public String getBotUsername () {
            return "@MyBookSearchBot";
        }

        @Override
        public String getBotToken () {
            return "1791148758:AAENguxobQLA1SASpkYP7_4nKUXs45ehdbc";
        }
    }
