public class Main {

    public static void main(String[] args) {
        /*

        Тестирование кода!!
         */

        Subtask first = new Subtask("First", Status.NEW);
        Subtask second = new Subtask("First", Status.NEW);
        Subtask third = new Subtask("Second", Status.NEW);
        Subtask fourth = new Subtask("First", Status.IN_PROGRESS);
        Subtask fifth = new Subtask("First", Status.DONE);

        System.out.println(first.equals(second)); // true
        System.out.println(first.getUid() == second.getUid()); // true
        System.out.println(first.equals(fourth)); // false
        System.out.println(first.getUid() == fourth.getUid()); // false
        System.out.println(first.equals(fifth)); // false
        System.out.println(first.getUid() == fifth.getUid()); // false
        System.out.println(first.equals(third)); // false
        System.out.println(first.getUid() == third.getUid()); // false

        first.setStatus(Status.IN_PROGRESS);
        System.out.println(first.equals(fourth)); // false
        System.out.println(first.getUid() == fourth.getUid()); // false



    }
}
