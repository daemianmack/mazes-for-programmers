This Clojure/Script project is written in CLJC, which allows for
compilation to both Clojure and Clojurescript.

### Clojure for a full-blown REPL
The minimal deps.edn allows invocation through the Clojure 
[binary]. This allows for a full-blown REPL experience with something
like CIDER in Emacs.

[binary]: https://clojure.org/guides/getting_started

#### Example REPL invocation
```
> clj -A:cider
```

This will print out the port on which nREPL is now listening for your
editor to connect to.

### ClojureScript for fast CLI iterations
A second, faster invocation path is through something like [lumo],
which can read the CLJC directly and offers ClojureScript-speed
command-line response times, just like any other command-line binary...

[lumo]:https://github.com/anmonteiro/lumo

#### Example Lumo invocation
`
> lumo -D cljsjs/seedrandom:3.0.5-0 -c src -m mfp.sidewinder
`

Typically I'd develop at a full editor-connected REPL, for maximum
exploratory reach, but since the sibling Ruby project's workflow lends
itself toward running a (fast) system binary, that's the mode I'm
leaning toward for the Clojure half of this project, too.
