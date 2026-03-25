//Леф, я прочитал про модификаторы доступа, понял зачем нужен паблик, приват, дефолт и
//протектед, но в контексте это работы я не вижу,  где бы мне могли пригодится эти модификаторы,
//тк код написан по сути своей в одном методе, что касается рефакторинга, мне кажется он тут не нужен,
//я бы вполне мог перенести все методы, работающие с месяцем в его класс, мб было бы почище...
//но код на 200 строк с допотопными методами(их всего 7) как будто и так легко восприним

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static FileReader fileReader = new FileReader();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ArrayList<Double> expensesY = new ArrayList<>();
        ArrayList<Double> incomeY = new ArrayList<>();
        Month[] months = new Month[12];

        while (true) {
            getMenu();
            int choise = scanner.nextInt();

            if (choise == 1) {
                for (int i = 1; i <= 12; i++) {
                    months[i - 1] = new Month();
                    months[i - 1].numberOfMonth = i;
                    transcriptM("m.2021" + String.format("%02d", i) + ".csv", i, months);
                }
            } else if (choise == 2) {
                transcriptY("y.2021.csv", expensesY, incomeY);
            } else if (choise == 3) {
                int status = examination(months, expensesY);
                if (status == 1) {
                    System.out.println("Нет необходимых данных, считайте месячные и годовые отчёты");
                } else {
                    compareMonthsVsYearly(months, expensesY, incomeY);
                }
            } else if (choise == 4) {
                    System.out.println("=== МЕСЯЧНЫЕ ОТЧЁТЫ ===");
                    for (int i = 0; i < 12; i++) {
                        if (months[i] != null) {
                            System.out.println("Месяц " + (i + 1) + ":");
                            System.out.println("  Самый прибыльный товар: " + months[i].mostProfitable +
                                " (" + months[i].sumProfit + " руб)");
                            System.out.println("  Самая большая трата: " + months[i].mostWaste +
                                " (" + months[i].sumWaste + " руб)");
                        } else {
                            System.out.println((i + 1) + " месяц ещё не загружен");
                        }
                }
            } else if (choise == 5) {
                if (expensesY.size() < 1) {
                    System.out.println("Сначала считайте годовой отчёт");
                } else {
                    System.out.println("=== ГОДОВОЙ ОТЧЁТ ===");
                    for (int i = 0; i < expensesY.size(); i++) {
                        System.out.println("В " + (i + 1) + " месяце было потрачено: " + expensesY.get(i));
                        System.out.println("заработанно: " + incomeY.get(i));
                    }
                System.out.println("Cредний расход за все имеющиеся операции в году: " + averageExpense(expensesY));
                System.out.println("Cредний доход за все имеющиеся операции в году: " + averageIncome(incomeY));
            }
            } else if (choise == 6) {
                System.out.println("Выход");
                break;
            } else {
                System.out.println("Такой команды пока нет");
            }
        }
    }

    public static void getMenu() {
        System.out.println(
            "Введите цифру, соответствующую тому, что бы вы хотели сделать:\n" +
                "1. Считать все месячные отчёты\n" +
                "2. Считать годовой отчёт\n" +
                "3. Сверить отчёты\n" +
                "4. Вывести информацию обо всех месячных отчётах\n" +
                "5. Вывести информацию о годовом отчёте\n" +
                "6. Выйти");
    }

    static void transcriptM(String fileName, int monthIndex, Month[] months) {
        ArrayList<String> lines = fileReader.readFileContentsM(fileName);
        if (!lines.isEmpty()) {
            lines.remove(0);
        }
        double expenses = 0;
        double income = 0;
        String mostProfitable = "";
        double sumProfit = 0;
        String mostWaste = "";
        double sumWaste = 0;
        for (String line : lines) {
            String[] lineContents = line.split(",");
            boolean isExpense = Boolean.parseBoolean(lineContents[1]);
            double quantity = Double.parseDouble(lineContents[2]);
            double price = Double.parseDouble(lineContents[3]);
            double currentSum = quantity * price;
            if (isExpense) {
                expenses += currentSum;
                if (currentSum > sumWaste) {
                    sumWaste = currentSum;
                    mostWaste = lineContents[0];
                }
            } else {
                income += currentSum;
                if (currentSum > sumProfit) {
                    sumProfit = currentSum;
                    mostProfitable = lineContents[0];
                }
            }
        }
        months[monthIndex - 1].expenses = expenses;
        months[monthIndex - 1].income = income;
        months[monthIndex - 1].mostProfitable = mostProfitable;
        months[monthIndex - 1].sumProfit = sumProfit;
        months[monthIndex - 1].mostWaste = mostWaste;
        months[monthIndex - 1].sumWaste = sumWaste;
        System.out.println("Данные за " + monthIndex + " месяц сохранены");
    }

    static void transcriptY(String fileName, ArrayList<Double> expensesY, ArrayList<Double> incomeY) {
        ArrayList<String> lines = fileReader.readFileContentsY(fileName);
        if (!lines.isEmpty()) {
            lines.remove(0);
        }
        for (String line : lines) {
            String[] lineContents = line.split(",");
            boolean isExpense = Boolean.parseBoolean(lineContents[2]);
            double amount = Double.parseDouble(lineContents[1]);
            if (isExpense) {
                expensesY.add(amount);
            } else {
                incomeY.add(amount);
            }
        }
        System.out.println("Данные сохранены");
    }

    static double averageExpense(ArrayList<Double> expensesY) {
        if (expensesY.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (double expense : expensesY) {
            sum += expense;
        }
        return sum / expensesY.size();
    }

    static double averageIncome(ArrayList<Double> incomeY) {
        if (incomeY.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (double income : incomeY) {
            sum += income;
        }
        return sum / incomeY.size();
    }

    static int examination(Month[] months, ArrayList<Double> expensesY) {
        boolean monthlyDataReady = months != null && months[0] != null && months[0].expenses != 0;
        boolean yearlyDataReady = expensesY != null && !expensesY.isEmpty() && expensesY.get(0) != 0;
        if (!monthlyDataReady || !yearlyDataReady) {

            return 1;
        } else {
            return 0;
        }
    }

    static void compareMonthsVsYearly(Month[] months, ArrayList<Double> expensesY, ArrayList<Double> incomeY) {
        for (int i = 0; i < expensesY.size(); i++) {
            boolean exp = months[i].expenses == expensesY.get(i).doubleValue();
            boolean inc = months[i].income == incomeY.get(i).doubleValue();

            if (exp) {
                System.out.println("Расходы за " + (i + 1) + " месяц сошлись");
            } else {
                System.out.println("Расходы за " + (i + 1) + " месяц расходятся");
            }
            if (inc) {
                System.out.println("Доходы за " + (i + 1) + " месяц сошлись");
            } else {
                System.out.println("Доходы за " + (i + 1) + " месяц расходятся");
            }
        }
    }
}