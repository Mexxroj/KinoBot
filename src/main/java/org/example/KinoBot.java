package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class KinoBot extends TelegramLongPollingBot {

    private static final String BOT_TOKEN = "6962519555:AAGYCduYrwvCS4F1MK5ZRhNn983ceOTbltQ";
    private static final String BOT_USERNAME = "Raqamli_kinolarr_bot";
    private static final String KINO_CHANNEL_LINK = "https://t.me/kodsiz_kinolar/23";

    private static final Map<String, String> CODE_TO_CHANNEL = new HashMap<>();
    private static final Set<String> USERS = new HashSet<>();

    static {
        CODE_TO_CHANNEL.put("1", "https://t.me/+CWJdeK1ddLwxMDYy");
        CODE_TO_CHANNEL.put("2", "https://t.me/+gmVWSAjjEWc2M2Uy");
        CODE_TO_CHANNEL.put("3", "https://t.me/+ghi789rst");
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            String chatId;
            String messageText;

            // Agar foydalanuvchi tugma bosgan bo'lsa (CallbackQuery)
            if (update.hasCallbackQuery()) {
                chatId = update.getCallbackQuery().getMessage().getChatId().toString();
                messageText = update.getCallbackQuery().getData(); // CallbackData ni olish
            } else if (update.hasMessage() && update.getMessage().hasText()) {
                chatId = update.getMessage().getChatId().toString();
                messageText = update.getMessage().getText().trim();
            } else {
                return;
            }

            // Yangi foydalanuvchi ro'yxatga olinadi
            if (!USERS.contains(chatId)) {
                USERS.add(chatId);
            }

            switch (messageText) {
                case "/start":
                    String userName = update.hasCallbackQuery() ?
                            update.getCallbackQuery().getFrom().getFirstName() :
                            update.getMessage().getFrom().getFirstName();
                    sendMessage(chatId, "Assalamu alaykum, " + userName + "! Men kino botman 🎬\n\n" +
                            "Kino ko'rish uchun kod yuboring yoki /help buyrug'idan foydalaning.");
                    break;
                case "/help":
                    sendHelpMessage(chatId);
                    break;
                case "/stat":
                    sendMessage(chatId, "📊 Botdan jami foydalanuvchilar soni: " + USERS.size());
                    break;
                case "/contact":
                    sendMessage(chatId, "📩 Savol yoki taklif uchun: @Mexxroj");
                    break;
                case "/about":
                    sendMessage(chatId, "🎬 Bu bot maxfiy kino kanallariga kirish uchun kod asosida havola beradi.");
                    break;
                default:
                    handleCode(chatId, messageText);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendHelpMessage(String chatId) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("📘 Yordam bo'limi:\nQuyidagi buyrug‘lardan foydalaning:");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // Har bir buyrug' uchun alohida tugma
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton startButton = new InlineKeyboardButton();
        startButton.setText("🚀 /start");
        startButton.setCallbackData("/start");
        row1.add(startButton);
        rowsInline.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton statButton = new InlineKeyboardButton();
        statButton.setText("📊 /stat");
        statButton.setCallbackData("/stat");
        row2.add(statButton);
        rowsInline.add(row2);

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton contactButton = new InlineKeyboardButton();
        contactButton.setText("📩 /contact");
        contactButton.setCallbackData("/contact");
        row3.add(contactButton);
        rowsInline.add(row3);

        List<InlineKeyboardButton> row4 = new ArrayList<>();
        InlineKeyboardButton aboutButton = new InlineKeyboardButton();
        aboutButton.setText("ℹ️ /about");
        aboutButton.setCallbackData("/about");
        row4.add(aboutButton);
        rowsInline.add(row4);

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        execute(message);
    }

    private void handleCode(String chatId, String code) throws TelegramApiException {
        String channelLink = CODE_TO_CHANNEL.get(code.toUpperCase());

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if (channelLink == null) {
            InlineKeyboardButton kinoButton = new InlineKeyboardButton();
            kinoButton.setText("Kinolar ro‘yxati 📜");
            kinoButton.setUrl(KINO_CHANNEL_LINK);
            rowInline.add(kinoButton);
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);

            message.setText("❌ Kod noto‘g‘ri yuborildi.\n\n/help buyrug‘idan foydalaning yoki kinolar ro'yxatini ko'ring:");
            message.setReplyMarkup(markupInline);
        } else {
            InlineKeyboardButton inlineButton = new InlineKeyboardButton();
            inlineButton.setText("Kinoni ko'rish 🎥");
            inlineButton.setUrl(channelLink);
            rowInline.add(inlineButton);
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            message.setText("Mana sizning kino havolangiz:");
            message.setReplyMarkup(markupInline);
        }

        execute(message);
    }

    private void sendMessage(String chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        execute(message);
    }
}