# lambdar-steamfeeds

A feed generation service for [Steam® store][1] search results.

[1]: http://store.steampowered.com

## Prerequisites

You will need [Leiningen][2] 1.7.0 or above installed.

[2]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## API

This service accepts the following parameters:

| Key    | Value                                                                                                             | Required |
|--------|-------------------------------------------------------------------------------------------------------------------|---------:|
| url    | An absolute URL of a Steam search results page (e.g. [http://store.steampowered.com/search/?sort_by=Released][3]) |      Yes |
| format | Feed format ('rss' or 'json'). The default value is 'rss'.                                                        |       No |

[3]: http://store.steampowered.com/search/?sort_by=Released

## License

Copyright © 2014 Naoto Yokoyama

Distributed under the Eclipse Public License, the same as Clojure.
See the file LICENSE.
