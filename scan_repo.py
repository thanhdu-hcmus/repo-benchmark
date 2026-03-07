import os

OUTPUT_FILE = "repo_dump.txt"

BINARY_EXTENSIONS = {
    ".png", ".jpg", ".jpeg", ".gif", ".exe", ".dll",
    ".class", ".jar", ".zip", ".tar", ".gz", ".pyc"
}

def is_binary(file):
    return os.path.splitext(file)[1].lower() in BINARY_EXTENSIONS


def write_tree(root_dir, output):
    output.write("===== DIRECTORY TREE =====\n")

    for root, dirs, files in os.walk(root_dir):

        # bỏ folder .git
        if ".git" in dirs:
            dirs.remove(".git")

        level = root.replace(root_dir, "").count(os.sep)
        indent = " " * 4 * level

        output.write(f"{indent}{os.path.basename(root)}/\n")

        subindent = " " * 4 * (level + 1)
        for f in files:
            output.write(f"{subindent}{f}\n")


def write_file_contents(root_dir, output):
    output.write("\n\n===== FILE CONTENTS =====\n")

    for root, dirs, files in os.walk(root_dir):

        # bỏ folder .git
        if ".git" in dirs:
            dirs.remove(".git")

        for file in files:

            if file == OUTPUT_FILE:
                continue

            if is_binary(file):
                continue

            path = os.path.join(root, file)

            output.write("\n" + "=" * 80 + "\n")
            output.write(f"FILE: {path}\n")
            output.write("=" * 80 + "\n")

            try:
                with open(path, "r", encoding="utf-8", errors="ignore") as f:
                    output.write(f.read())
                    output.write("\n")
            except Exception as e:
                output.write(f"Cannot read file: {e}\n")


def main():
    root_dir = os.getcwd()

    with open(OUTPUT_FILE, "w", encoding="utf-8") as output:
        output.write(f"Repository scan: {root_dir}\n\n")

        write_tree(root_dir, output)
        write_file_contents(root_dir, output)

    print(f"Done! Output written to {OUTPUT_FILE}")


if __name__ == "__main__":
    main()