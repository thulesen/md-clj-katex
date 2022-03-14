# Installation

Execute
    npm install
to install dependencies, including `shadow-cljs`.

# Usage

## Interactive App

Add some markdown files to `src/md` and execute
    npx shadow-cljs watch app
Then the compiled markdown will be available at localhost:8080. The directory
structure will be preserved so that `src/md/path/to/example.md` is accessed by
`localhost:8000/path/to/example`. The server watches for changes to all the
files in `src/md`, but it needs to be restarted when new files are added.

## Node Script

The node script can be compiled with
    npx shadow-cljs release script
and then it can be used as
    chmod +x out/script.js
    ./out/script.js in-dir out-dir
where `in-dir` is a directory with markdown files, and `out-dir` is a directory
that after execution of the script will contain the corresponding html files.
