import numpy as np


def calculate_percentiles(data):
    p50 = np.percentile(data, 50)
    p90 = np.percentile(data, 90)
    p99 = np.percentile(data, 99)
    p100 = np.percentile(data, 100)
    return p50, p90, p99, p100


def read_integers_from_file(file_path):
    with open(file_path, "r") as file:
        lines = file.readlines()
        data = [int(line.strip()) for line in lines]
    return data


def main():
    for file_path in ["jdk8.txt", "jdk17.txt"]:
        print(f"Processing {file_path}")
        data = read_integers_from_file(file_path)

        if not data:
            print("Error: No data found in the file.")
            return

        sorted_data = sorted(data)
        p50, p90, p99, p100 = calculate_percentiles(sorted_data)

        print(f"p50 latency: {p50}")
        print(f"p90 latency: {p90}")
        print(f"p99 latency: {p99}")
        print(f"p100 latency: {p100}")


if __name__ == "__main__":
    main()
