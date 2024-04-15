import os
import requests
import concurrent.futures
import threading  # Import the threading module

# Lock to synchronize access to the output file
output_lock = threading.Lock()


def get_dependencies(package_name):
    url = f"https://registry.npmjs.org/{package_name}"
    response = requests.get(url)
    if response.status_code == 200:
        data = response.json()
        if 'versions' in data:
            # Get the latest version
            latest_version_info = data['versions'][data['dist-tags']['latest']]
            if 'dependencies' in latest_version_info:
                dependencies = latest_version_info['dependencies']
                return list(dependencies.keys())
    return []


def process_package(package_name):
    print(f"Fetching dependencies for {package_name}...")
    dependencies = get_dependencies(package_name)
    print(f"Found dependencies for {package_name}: {dependencies}")

    with output_lock:
        # Write dependencies to the output file
        with open(output_file_path, 'a') as output_file:
            output_file.write(f"{package_name} -> {dependencies}\n")


def main():
    # Get the full path to the package_list.txt file
    script_dir = os.path.dirname(os.path.abspath(__file__))
    package_list_path = os.path.join(script_dir, 'package_list.txt')

    print("Reading package names from the text file...")
    # Read package names from the text file
    with open(package_list_path, 'r') as file:
        package_names = [line.strip() for line in file.readlines()]

    print(f"Total {len(package_names)} package names read.")

    print("Finding dependencies...")

    # Create the output file path
    global output_file_path
    output_file_path = os.path.join(script_dir, 'dependencies_output.txt')

    # Clear the output file if it exists
    if os.path.exists(output_file_path):
        os.remove(output_file_path)

    # Use a thread pool executor to limit the number of concurrent threads
    with concurrent.futures.ThreadPoolExecutor(max_workers=10) as executor:
        # Submit each package for processing
        futures = {executor.submit(process_package, package_name): package_name for package_name in package_names}

        # Wait for all tasks to complete
        for future in concurrent.futures.as_completed(futures):
            package_name = futures[future]
            try:
                future.result()
            except Exception as e:
                print(f"Error processing {package_name}: {e}")

    print("Dependencies saved successfully.")


if __name__ == "__main__":
    main()
