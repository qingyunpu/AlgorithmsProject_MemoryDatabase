import csv
import time

# Linked List Implementation
class Node:
    def __init__(self, data):
        self.data = data
        self.next = None


class LinkedList:
    def __init__(self):
        self.head = None

    def append(self, data):
        new_node = Node(data)
        if not self.head:
            self.head = new_node
            return
        curr = self.head
        while curr.next:
            curr = curr.next
        curr.next = new_node

    def to_list(self):
        result = []
        curr = self.head
        while curr:
            result.append(curr.data)
            curr = curr.next
        return result

    def from_list(self, lst):
        self.head = None
        for item in lst:
            self.append(item)


# Sorting Algorithms

def bubble_sort(arr, key, reverse=False):
    n = len(arr)
    for i in range(n):
        for j in range(0, n - i - 1):
            if (arr[j][key] > arr[j + 1][key]) ^ reverse:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
    return arr


def insertion_sort(arr, key, reverse=False):
    for i in range(1, len(arr)):
        temp = arr[i]
        j = i - 1
        while j >= 0 and ((arr[j][key] > temp[key]) ^ reverse):
            arr[j + 1] = arr[j]
            j -= 1
        arr[j + 1] = temp
    return arr


def merge_sort(arr, key, reverse=False):
    if len(arr) <= 1:
        return arr

    mid = len(arr) // 2
    left = merge_sort(arr[:mid], key, reverse)
    right = merge_sort(arr[mid:], key, reverse)

    return merge(left, right, key, reverse)


def merge(left, right, key, reverse):
    result = []
    i = j = 0
    while i < len(left) and j < len(right):
        if (left[i][key] <= right[j][key]) ^ reverse:
            result.append(left[i])
            i += 1
        else:
            result.append(right[j])
            j += 1
    result.extend(left[i:])
    result.extend(right[j:])
    return result


def quick_sort(arr, key, reverse=False):
    if len(arr) <= 1:
        return arr
    pivot = arr[len(arr) // 2][key]
    left = [x for x in arr if (x[key] < pivot) ^ reverse]
    middle = [x for x in arr if x[key] == pivot]
    right = [x for x in arr if (x[key] > pivot) ^ reverse]
    return quick_sort(left, key, reverse) + middle + quick_sort(right, key, reverse)


# Recursive CSV Export

def recursive_csv_writer(data, filename, index=0, header=None, mode="w"):
    if index == 0 and header:
        with open(filename, mode, newline='') as f:
            writer = csv.DictWriter(f, fieldnames=header)
            writer.writeheader()
    if index >= len(data):
        return
    with open(filename, "a", newline='') as f:
        writer = csv.DictWriter(f, fieldnames=header)
        writer.writerow(data[index])
    recursive_csv_writer(data, filename, index + 1, header, "a")



# Query Execution

def execute_query(query, linkedlist, headers):
    query = query.strip().lower()

    if not query.startswith("select"):
        print("Invalid query. Must start with SELECT.")
        return

   
    try:
        select_part = query.split("from")[0].replace("select", "").strip()
        cols = [c.strip() for c in select_part.split(",")]

        order_part = query.split("order by")[1].strip()
        tokens = order_part.split()

        order_col = tokens[0]
        order_dir = tokens[1].upper()


        if "with" in tokens:
            sorting_algo = tokens[tokens.index("with") + 1]
        else:
            sorting_algo = tokens[2] 

    except Exception as e:
        print("Query parsing error:", e)
        return

    
    data = linkedlist.to_list()

   
    reverse = True if order_dir == "DSC" else False

    if sorting_algo == "bubble_sort":
        data = bubble_sort(data, order_col, reverse)
    elif sorting_algo == "insertion_sort":
        data = insertion_sort(data, order_col, reverse)
    elif sorting_algo == "merge_sort":
        data = merge_sort(data, order_col, reverse)
    elif sorting_algo == "quick_sort":
        data = quick_sort(data, order_col, reverse)
    else:
        print("Unsupported sorting algorithm")
        return

    # Filter columns
    if cols == ["*"]:
        result = data
    else:
        result = [{col: row[col] for col in cols} for row in data]

    # Export
    filename = f"output_{int(time.time())}.csv"
    recursive_csv_writer(result, filename, 0, header=result[0].keys())
    print(f"Query executed. Results saved to {filename}")
    # recursive_csv_writer(result, "output.csv", 0, header=result[0].keys())
    # print("Query executed. Results saved to output.csv")

    return result



# Main

if __name__ == "__main__":
    filename = "student-data.csv"

    # Load CSV into linked list
    ll = LinkedList()
    with open(filename, "r") as f:
        reader = csv.DictReader(f)
        headers = reader.fieldnames
        for row in reader:
            ll.append(row)

    print("✅ Loaded student-data.csv into memory database.")

    while True:
        query = input("\nEnter SQL-like query (or 'exit' to quit):\n> ")
        if query.lower() == "exit":
            break
        result = execute_query(query, ll, headers)
        if result:
            print("Top 5 rows:", result[:5])
