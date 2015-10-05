# script-module

FIXME: description

## Installation

Download from http://example.com/FIXME.

## Usage

FIXME: explanation

    $ java -jar script-module-0.1.0-standalone.jar [args]



## Configuration

{"script" {
    "max-wait" 120,

    "base-paths" {
      "python" "/home/ruguer/project/clojure/bandit-project/bandit-script/resources/python",
      "ruby"   "/home/ruguer/project/clojure/bandit-project/bandit-script/resources/ruby"
    }
    "environment" {
      "happy" 12,
      "sad" "32",
      "linkedList" (let [l (java.util.LinkedList.)]
                     (.add l 1)
                     (.add l 2)
                     (.add l 3)
                     l)
    }
  }
}

- *max-wait*: Time in seconds, max, to wait for.
- *base-paths*: [engine path] pairs for where scripts are stored.
- *environment*: Variable names and values to import statically into
   scripts. Useful for providing by-environment bindings for scripts,
   and reducing dependence on the OS.

## Examples

### Jython Script
<pre>
print "args: " + str(args)
print "hello: " + str(happy + 12)
print "sad: " + sad
print "linkedList: " + str(linkedList)
</pre>
### Bandit Call
<pre>
Test jython|script|run|python|python1.py|candy
</pre>

### JRuby Script
<pre>
puts "argument 1 is:" + $args.get(1)
</prc>

Notice that a $ is required prior to the argument.

### Bandit Call
<pre>
Correct number of arguments|script|run|ruby|test3.rb|one|four
Incorrect number of arguments|script|run|ruby|test3.rb|one
</pre>


### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
