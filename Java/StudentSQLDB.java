/*
 * StudentSQLDB.java
 *
 * Features
 *  - Load CSV into an in-memory linked list
 *  - Insertion sort (linked-list relink)
 *  - Quick sort (linked-list recursive partition)
 *  - Minimal SQL:
 *      select c1, c2, ... from t1 order by cX ASC/DSC with insertion_sort|quick_sort;
 *  - Recursive CSV export via: output <file>
 *    (exports ONLY the last query's SELECT columns; falls back to all columns if no query yet)
 *
 * Usage
 *   javac StudentSQLDB.java
 *   java StudentSQLDB student-data.csv
 *   # REPL examples:
 *   select school,sex,age from t1 order by age ASC with insertion_sort;
 *   output out.csv
 *   select school,sex,age from t1 order by age DSC with quick_sort;
 *   output out2.csv
 *   quit
 */

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class StudentSQLDB {

    /* ================== Linked List Node ================== */
    static final class Node {
        String[] row;
        Node next;
        Node(String[] r) { this.row = r; }
    }

    /* ================== Memory DB ================== */
    static final class MemDB {
        String[] header = new String[0];
        Node head = null;

        void loadCSV(String path) throws IOException {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
                String first = br.readLine();
                if (first == null) { header = new String[0]; head = null; return; }
                header = splitCsvSmart(first);
                List<String[]> rows = new ArrayList<>();
                String line;
                while ((line = br.readLine()) != null) rows.add(splitCsvSmart(line));
                head = buildRecursive(rows, 0);
            }
        }

        private Node buildRecursive(List<String[]> rows, int i) {
            if (i >= rows.size()) return null;
            Node n = new Node(rows.get(i));
            n.next = buildRecursive(rows, i + 1);
            return n;
        }

        int colIndex(String name) {
            String want = name.trim();
            for (int i = 0; i < header.length; i++) if (header[i].equals(want)) return i;
            String lw = want.toLowerCase(Locale.ROOT);
            for (int i = 0; i < header.length; i++)
                if (header[i].trim().toLowerCase(Locale.ROOT).equals(lw)) return i;
            throw new IllegalArgumentException("Column not found: " + name);
        }

        boolean isNumericColumn(int j) {
            Node cur = head; int total=0, numeric=0;
            while (cur != null && total < 50) {
                try { Double.parseDouble(cur.row[j]); numeric++; } catch (Exception ignore) {}
                total++; cur = cur.next;
            }
            return numeric >= Math.max(1, total/2);
        }

        /* ---------- Insertion Sort (relink) ---------- */
        void insertionSort(String colName, boolean asc) {
            if (head == null) return;
            int j = colIndex(colName);
            boolean isNum = isNumericColumn(j);

            Node sorted = null, cur = head;
            while (cur != null) {
                Node nxt = cur.next; cur.next = null;
                sorted = insertSorted(sorted, cur, j, isNum, asc);
                cur = nxt;
            }
            head = sorted;
        }

        private Node insertSorted(Node sorted, Node n, int j, boolean isNum, boolean asc) {
            if (sorted == null || compare(n.row, sorted.row, j, isNum, asc) < 0) {
                n.next = sorted; return n;
            }
            Node p = sorted;
            while (p.next != null && compare(n.row, p.next.row, j, isNum, asc) >= 0) p = p.next;
            n.next = p.next; p.next = n;
            return sorted;
        }

        /* ---------- Quick Sort (linked-list recursive partition) ---------- */
        void quickSort(String colName, boolean asc) {
            if (head == null) return;
            int j = colIndex(colName);
            boolean isNum = isNumericColumn(j);
            head = quickSortRec(head, j, isNum, asc);
        }

        private Node quickSortRec(Node start, int j, boolean isNum, boolean asc) {
            if (start == null || start.next == null) return start;
            Node pivot = start; Node cur = start.next; pivot.next = null;
            Node lessH=null, lessT=null, geH=null, geT=null;

            while (cur != null) {
                Node nxt = cur.next; cur.next = null;
                if (compare(cur.row, pivot.row, j, isNum, asc) < 0) {
                    if (lessH == null) { lessH = lessT = cur; } else { lessT.next = cur; lessT = cur; }
                } else {
                    if (geH == null) { geH = geT = cur; } else { geT.next = cur; geT = cur; }
                }
                cur = nxt;
            }

            lessH = quickSortRec(lessH, j, isNum, asc);
            geH   = quickSortRec(geH,   j, isNum, asc);

            Node headNew = (lessH != null) ? lessH : pivot;
            if (lessH != null) {
                Node t = lessH; while (t.next != null) t = t.next; t.next = pivot;
            }
            pivot.next = geH;
            return headNew;
        }

        /* ---------- Compare helper ---------- */
        private int compare(String[] a, String[] b, int j, boolean isNum, boolean asc) {
            int c;
            if (isNum) {
                double da = parseOrNaN(a[j]), db = parseOrNaN(b[j]);
                c = Double.compare(da, db);
            } else {
                c = a[j].compareTo(b[j]);
            }
            return asc ? c : -c;
        }
        private double parseOrNaN(String s) {
            try { return Double.parseDouble(s); } catch (Exception e) { return Double.NaN; }
        }

        /* ---------- Print selected columns ---------- */
        void printSelected(List<Integer> idx) {
            // header
            for (int i = 0; i < idx.size(); i++) {
                if (i > 0) System.out.print(",");
                System.out.print(header[idx.get(i)]);
            }
            System.out.println();
            // rows
            for (Node n = head; n != null; n = n.next) {
                for (int i = 0; i < idx.size(); i++) {
                    if (i > 0) System.out.print(",");
                    System.out.print(escapeCsv(n.row[idx.get(i)]));
                }
                System.out.println();
            }
        }

        /* ---------- Recursive CSV Export (selected columns) ---------- */
        void exportCSV(String path, List<Integer> idx) throws IOException {
            try (PrintWriter pw = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))) {
                // header
                for (int i = 0; i < idx.size(); i++) {
                    if (i > 0) pw.print(",");
                    pw.print(header[idx.get(i)]);
                }
                pw.println();
                // rows (recursive)
                writeRec(head, idx, pw);
            }
        }

        private void writeRec(Node n, List<Integer> idx, PrintWriter pw) {
            if (n == null) return;
            for (int i = 0; i < idx.size(); i++) {
                if (i > 0) pw.print(",");
                pw.print(escapeCsv(n.row[idx.get(i)]));
            }
            pw.println();
            writeRec(n.next, idx, pw);
        }

        int countRows() { int c=0; for (Node t=head; t!=null; t=t.next) c++; return c; }
    }

    /* ================== SQL Model & Parser ================== */
    static final class SQL {
        final List<String> cols;
        final String orderCol;
        final boolean asc;
        final String algo;
        SQL(List<String> c, String oc, boolean a, String alg) { cols=c; orderCol=oc; asc=a; algo=alg; }
    }

    // Grammar:
    //   select c1, c2, ... from t1 order by cX ASC/DSC with insertion_sort|quick_sort;
    static SQL parseSQL(String sql) {
        String s = sql.trim();
        if (s.endsWith(";")) s = s.substring(0, s.length()-1);
        s = s.replaceAll("\\s+,\\s+", ",");
        String lower = s.toLowerCase(Locale.ROOT);

        if (!lower.startsWith("select ")) throw new IllegalArgumentException("Not a SELECT");
        int fromPos = lower.indexOf(" from ");
        if (fromPos < 0) throw new IllegalArgumentException("Missing 'from'");
        int obPos = lower.indexOf(" order by ", fromPos);
        if (obPos < 0) throw new IllegalArgumentException("Missing 'order by'");
        int withPos = lower.indexOf(" with ", obPos);
        if (withPos < 0) throw new IllegalArgumentException("Missing 'with <algo>'");

        // SELECT columns
        String selectPart = s.substring("select ".length(), fromPos).trim();
        List<String> cols = new ArrayList<>();
        for (String c : selectPart.split(",")) {
            String t = c.trim();
            if (!t.isEmpty()) cols.add(t);
        }
        if (cols.isEmpty()) throw new IllegalArgumentException("No columns in SELECT");

        // ORDER BY
        String ob = s.substring(obPos + " order by ".length(), withPos).trim();
        String[] obTokens = ob.split("\\s+");
        if (obTokens.length < 1) throw new IllegalArgumentException("Missing order-by column");
        String orderCol = obTokens[0].trim().replaceAll(",$", "");
        boolean asc = true;
        if (obTokens.length >= 2) {
            String dir = obTokens[1].trim().toUpperCase(Locale.ROOT);
            asc = !(dir.equals("DSC") || dir.equals("DESC"));
        }

        // WITH algorithm
        String algo = s.substring(withPos + " with ".length()).trim().toLowerCase(Locale.ROOT);
        if (!(algo.equals("insertion_sort") || algo.equals("quick_sort"))) {
            throw new IllegalArgumentException("Unknown sorting algorithm: " + algo);
        }

        return new SQL(cols, orderCol, asc, algo);
    }

    /* ================== CSV helpers ================== */
    static String[] splitCsvSmart(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQ = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQ && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"'); i++; // escaped quote
                } else {
                    inQ = !inQ;
                }
            } else if (ch == ',' && !inQ) {
                out.add(sb.toString()); sb.setLength(0);
            } else {
                sb.append(ch);
            }
        }
        out.add(sb.toString());
        return out.toArray(new String[0]);
    }

    static String escapeCsv(String v) {
        if (v == null) return "";
        if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
            return "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }

    /* ================== Main (REPL) ================== */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java StudentSQLDB <csv-file>");
            return;
        }
        MemDB db = new MemDB();
        db.loadCSV(args[0]);

        // remember last SELECT projection for output
        List<Integer> lastSelectedIdx = null;

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        System.out.println("[Init] Loaded rows: " + db.countRows());
        System.out.println("[Commands]: rows | select ...; | output <file> | quit");
        System.out.println("Examples:");
        System.out.println("select school,sex,age from t1 order by age ASC with insertion_sort;");
        System.out.println("select school,sex,age from t1 order by age DSC with quick_sort;");
        System.out.println("output out.csv");
        String line;

        while (true) {
            System.out.print("> ");
            line = in.readLine();
            if (line == null) break;
            line = line.trim();
            if (line.isEmpty()) continue;

            String lwr = line.toLowerCase(Locale.ROOT);
            try {
                if (lwr.equals("quit")) {
                    break;
                } else if (lwr.equals("rows")) {
                    System.out.println("Rows in memory: " + db.countRows());
                } else if (lwr.startsWith("output ")) {
                    String[] parts = line.split("\\s+", 2);
                    if (parts.length < 2 || parts[1].trim().isEmpty()) {
                        System.out.println("Usage: output <file>");
                        continue;
                    }
                    String outFile = parts[1].trim();
                    List<Integer> idxToUse = lastSelectedIdx;
                    if (idxToUse == null) {
                        // fallback: export all columns if no SELECT yet
                        idxToUse = new ArrayList<>();
                        for (int i = 0; i < db.header.length; i++) idxToUse.add(i);
                        System.out.println("(no prior SELECT — exporting ALL columns)");
                    }
                    db.exportCSV(outFile, idxToUse);
                    System.out.println("Exported to: " + outFile);
                } else if (lwr.startsWith("select ")) {
                    SQL q = parseSQL(line);
                    if (q.algo.equals("insertion_sort")) db.insertionSort(q.orderCol, q.asc);
                    else if (q.algo.equals("quick_sort")) db.quickSort(q.orderCol, q.asc);

                    // build projection indexes & print result
                    List<Integer> idx = new ArrayList<>();
                    for (String c : q.cols) idx.add(db.colIndex(c));
                    db.printSelected(idx);

                    // remember projection for subsequent 'output'
                    lastSelectedIdx = idx;
                } else {
                    System.out.println("Unknown command");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }
}

