# Installation

Execute

    npm install

to install dependencies, including `shadow-cljs`.

# Usage

## Interactive App

Execute

    npx shadow-cljs watch app

Then there will be an example page at `localhost:8000/hello-world`. The markdown
source for this page is at `src/md/hello-world.md`. You can save your own
markdown files in `src/md` and they will be available so that
`localhost:8000/path/to/page` refers to html generated from
`src/md/path/to/page.md`. Hot-reloading is enabled so updating the markdown
files automatically means the html pages are updated, but adding a new file
requires a recompilation. Trigger one by executing

    npm run force-compile

## Node Script

The node script can be compiled with

    npx shadow-cljs release script

and then it can be used as

    chmod +x out/script.js
    ./out/script.js in-dir out-dir

where `in-dir` is a directory with markdown files, and `out-dir` is a directory
that after execution of the script will contain the corresponding html files.
