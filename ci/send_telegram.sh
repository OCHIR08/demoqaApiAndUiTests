#!/bin/bash
set -e

# Путь к результатам JUnit (Gradle кладет их сюда)
RESULTS_DIR="build/test-results/test"
ALLURE_DIR="build/allure-report"

# Читаем статистику по тестам
TOTAL=$(grep -o 'testsuite' $RESULTS_DIR/*.xml | wc -l)
FAILED=$(grep -o 'failure' $RESULTS_DIR/*.xml | wc -l)
ERRORS=$(grep -o 'error' $RESULTS_DIR/*.xml | wc -l)
SKIPPED=$(grep -o 'skipped' $RESULTS_DIR/*.xml | wc -l)

MESSAGE="✅ Тесты завершены
Всего: $TOTAL
❌ Failed: $FAILED
⚠️ Errors: $ERRORS
⏭ Skipped: $SKIPPED"

TG_BOT_TOKEN = "8398899834:AAGaJFN-LRyysqkCERcw87G1ihKkaof3AaM"
TG_CHAT_ID = "505918790"
# Отправка текста в Telegram
curl -s -X POST "https://api.telegram.org/bot${TG_BOT_TOKEN}/sendMessage" \
     -d chat_id="${TG_CHAT_ID}" \
     -d text="$MESSAGE"

# Отправка Allure-отчёта архивом (если нужно)
if [ -d "$ALLURE_DIR" ]; then
    zip -r allure-report.zip $ALLURE_DIR
    curl -s -X POST "https://api.telegram.org/bot${TG_BOT_TOKEN}/sendDocument" \
         -F chat_id="${TG_CHAT_ID}" \
         -F document=@allure-report.zip
fi
