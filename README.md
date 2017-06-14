# Conference_Organization
System wspomagający organizację konferencji 
<br>
Dokumentacja: Piotr Wieczorek
<br>
<br>
<b>Opis zadania</b> <br>
Jesteś odpowiedzialny za implementację systemu wspomagającego organizację i przeprowadzenie konferencji. Twoim zadaniem jest zaimplementowanie zdefiniowanego poniżej API.
System ma udostępniać API niezależnym aplikacjom działającym na urządzeniach mobilnych uczestników konferencji. Niemniej jednak, ze względu na to, że interesuje nas tematyka baz danych przedmiotem projektu jest stworzenie wersji deweloperskiej systemu, w której wywołania API będą wczytywane z dostarczonego pliku.
<br>
<br>
<b>Opis problemu</b><br>
Konferencja obejmuje od kilku do kilkunastu wydarzeń, z których każde zawiera do kilkudziesięciu referatów. Każde wydarzenie posiada datę rozpoczęcia i zakończenia, wydarzenia mogą się pokrywać.
<br>
<br>
Uczestnicy (kilkaset osób) rejestrują się na dowolnie wybrane przez siebie wydarzenia. Każdy uczestnik może wygłaszać dowolną liczbę referatów (być może 0). Ponadto uczestnicy mogą nawiązywać (symetryczne) znajomości z innymi uczestnikami.
<br>
<br>
Uczestnicy korzystają z systemu poprzez aplikację na urządzeniu mobilnym, która umożliwia przeglądanie danych konferencji, planu wydarzeń dla danego uczestnika, a także proponowanie i dowiadywanie się o dodatkowych referatach organizowanych spontanicznie – uczestnik proponuje referat, organizator konferencji go zatwierdza, przydziela salę oraz upublicznia. Aplikacja rejestruje obecność uczestnika na poszczególnych referatach, uczestnik może ocenić każdy z referatów (niezależnie od tego czy referat się już odbył). Każdy uczestnik posiada unikalny login i hasło.
<br>
<br>
Organizator konferencji może definiować wydarzenia i ich zawartość, przeglądać wszystkie zbierane dane, w tym również rozmaite statystyki dotyczące aktywności uczestników.
<br>
<br>
Twój program po uruchomieniu powinien przeczytać ze standardowego wejścia ciąg wywołań funkcji API, a wyniki ich działania wypisać na standardowe wyjście.
<br>
<br>
Wszystkie dane powinny być przechowywane w bazie danych, efekt działania każdej funkcji modyfikującej bazę, dla której wypisano potwierdzenie wykonania (wartość OK) powinien być utrwalony. Program może być uruchamiany wielokrotnie np. najpierw wczytanie pliku z danymi początkowymi, a następnie kolejnych testów poprawnościowych. Przy pierwszym uruchomieniu program powinien utworzyć wszystkie niezbędne elementy bazy danych (tabele, więzy, funkcje wyzwalacze) zgodnie z przygotowanym przez studenta modelem fizycznym. Baza nie będzie modyfikowana pomiędzy kolejnymi uruchomieniami. Program nie będzie miał praw do  tworzenia i zapisywania jakichkolwiek plików. Program będzie mógł czytać pliki z bieżącego katalogu.
<br>
<br>
<b>Format pliku wejściowego</b>
<br>
Każda linia pliku wejściowego zawiera obiekt JSON (http://www.json.org/json-pl.html). Każdy z obiektów opisuje wywołanie jednej funkcji API wraz z argumentami.
<br>
Przykład: <br>
obiekt { "function": { "arg1": "val1", "arg2": "val2" } } <br> oznacza wywołanie funkcji o nazwie function z argumentem arg1 przyjmującym wartość "val1" oraz arg2 – "val2".
<br>
W pierwszej linii wejścia znajduje się wywołanie funkcji open z argumentami umożliwiającymi nawiązanie połączenia z bazą danych.
<br>
<br>
<b>Format wyjścia</b>
<br>
Dla każdego wywołania wypisz w osobnej linii obiekt JSON zawierający status wykonania funkcji OK/ERROR/NOT IMPLEMENTED oraz zwracane dane wg specyfikacji tej funkcji.
<br>
Format zwracanych danych:
<br>
{ "status":"OK","data": [ { "atr1":"v1","atr2":"v2" },{ "atr1":"v3","atr2":"v3" } ]} <br>
Tabela data zawiera wszystkie wynikowe krotki. Każda krotka zawiera wartości dla wszystkich atrybutów.
<br> <br>
<b>Format opisu API</b>
<br>
Oznaczenia:
<br>
<pre>
O – wymaga autoryzacji jako organizator, <br>
U – wymaga autoryzacji jako zwykły uczestnik,<br>
N – nie wymaga autoryzacji, <br>
(*) - wymagana na zaliczenie <br>

[function] [arg1] [arg2] … [argn] 
 nazwa funkcji oraz nazwy jej argumentów
</pre>
<br>
<br>
<b>Nawiązywanie połączenia i definiowanie danych organizatora</b>
<br>
<pre>
<b>(*) open [baza] [login] [password]</b>
 przekazuje dane umożliwiające podłączenie Twojego programu do bazy - nazwę bazy, 
 login oraz hasło, wywoływane dokładnie jeden raz, w pierwszej linii wejścia<br>
 zwraca status OK/ERROR w zależności od tego czy udało się nawiązać połączenie z bazą

<b>(*) organizer [secret] [newlogin] [newpassword] </b>
 tworzy uczestnika [newlogin] z uprawnieniami organizatora i hasłem [newpassword], 
 argument [secret] musi być równy d8578edf8458ce06fbc5bb76a58c5ca4 
 zwraca status OK/ERROR 

</pre>
</b>
</b>
<b>Operacje modyfikujące bazę</b>

Każde z poniższych wywołań powinno zwrócić obiekt JSON zawierający wyłącznie status wykonania: OK/ERROR/NOT IMPLEMENTED.
</b>
<pre>
<b>(*O) event [login] [password] [eventname] [start_timestamp] [end_timestamp] </b> 
 rejestracja wydarzenia, napis <eventname> jest unikalny

<b>(*O) user [login] [password] [newlogin] [newpassword] </b> 
 rejestracja nowego uczestnika <login> i <password> służą do autoryzacji wywołującego 
 funkcję, który musi posiadać uprawnienia organizatora, <newlogin> <newpassword> są danymi 
 nowego uczestnika, <newlogin> jest unikalny

<b>(*O) talk [login] [password] [speakerlogin] [talk] [title] [start_timestamp] [room] [initial_evaluation] [eventname] </b> 
 rejestracja referatu/zatwierdzenie referatu spontanicznego, <talk> jest unikalnym identyfikatorem referatu,
 <initial_evaluation> jest oceną organizatora w skali 0-10 – jest to ocena traktowana tak samo jak ocena
 uczestnika obecnego na referacie, <eventname> jest nazwą wydarzenia, którego częścią jest dany 
 referat - może być pustym napisem, co oznacza, że referat nie jest przydzielony do jakiegokolwiek 
 wydarzenia

<b>(*U) register_user_for_event [login] [password] [eventname] </b> 
 rejestracja uczestnika [login] na wydarzenie <eventname>

<b>(*U) attendance [login] [password] [talk] </b>
 odnotowanie faktycznej obecności uczestnika [login] na referacie [talk]

<b>(*U) evaluation [login] [password] [talk] [rating] </b>
 ocena referatu [talk] w skali 0-10 przez uczestnika [login]

<b>(O) reject [login] [password] [talk] </b>
 usuwa referat spontaniczny <talk> z listy zaproponowanych,

<b>(U) proposal  [login] [password] [talk] [title] [start_timestamp] </b>
 propozycja referatu spontanicznego, [talk] - unikalny identyfikator referatu

<b>(U) friends [login1] [password] [login2] </b>
 uczestnik [login1] chce nawiązać znajomość z uczestnikiem [login2], znajomość uznajemy 
 za nawiązaną jeśli obaj uczestnicy chcą ją nawiązać tj. po wywołaniach 
 friends [login1] [password1] [login2] i friends [login2] [password2] [login1]
</pre>
<br>
<br>
<b>Pozostałe operacje</b>
<br>
Każde z poniższych wywołań powinno zwrócić obiekt JSON zawierający status wykonania OK/ERROR, a także tabelę data zawierającą krotki wartości atrybutów wg specyfikacji poniżej.


<pre>
<b>(*N) user_plan [login] [limit] </b>
 zwraca plan najbliższych referatów z wydarzeń, na które dany uczestnik jest zapisany 
 (wg rejestracji na wydarzenia) posortowany wg czasu rozpoczęcia, wypisuje pierwsze <limit> 
 referatów, przy czym 0 oznacza, że należy wypisać wszystkie
 Atrybuty zwracanych krotek: 
   [login] [talk] [start_timestamp] [title] [room]

<b>(*N) day_plan [timestamp] </b>
 zwraca listę wszystkich referatów zaplanowanych na dany dzień posortowaną rosnąco wg sal, w 
 drugiej kolejności wg czasu rozpoczęcia
 [talk] [start_timestamp] [title] [room]

<b>(*N) best_talks [start_timestamp] [end_timestamp] [limit] [all] </b> 
 zwraca referaty rozpoczynające się w  danym przedziale czasowym posortowane malejąco wg średniej oceny 
 uczestników, przy czym jeśli <all> jest równe 1 należy wziąć pod uwagę wszystkie oceny, w przeciwnym 
 przypadku tylko oceny uczestników, którzy byli na referacie obecni, wypisuje pierwsze <limit> referatów,
 przy czym 0 oznacza, że należy wypisać wszystkie
 [talk] [start_timestamp] [title] [room]

<b>(*N) most_popular_talks [start_timestamp] [end_timestamp] [limit] </b>
 zwraca referaty rozpoczynające się w podanym przedziału czasowego posortowane malejąco wg obecności, 
 wypisuje pierwsze <limit> referatów, przy czym 0 oznacza, że należy wypisać wszystkie
 [talk] [start_timestamp] [title] [room]

<b>(*U) attended_talks [login] [password]</b> 
 zwraca dla danego uczestnika referaty, na których był obecny 
 [talk] [start_timestamp] [title] [room]

<b>(*O) abandoned_talks [login] [password]  [limit] </b>
 zwraca listę referatów posortowaną malejąco wg liczby uczestników <number> zarejestrowanych na 
 wydarzenie obejmujące referat, którzy nie byli na tym referacie obecni, wypisuje pierwsze <limit> 
 referatów, przy czym 0 oznacza, że należy wypisać wszystkie
 [talk] [start_timestamp] [title] [room] [number]

<b>(N) recently_added_talks [limit] </b>
 zwraca listę ostatnio zarejestrowanych referatów, wypisuje ostatnie <limit> referatów wg daty 
 zarejestrowania, przy czym 0 oznacza, że należy wypisać wszystkie
 [talk] [speakerlogin] [start_timestamp] [title] [room]

<b>(U/O) rejected_talks [login] [password] </b>
 jeśli wywołujący ma uprawnienia organizatora zwraca listę wszystkich odrzuconych referatów 
 spontanicznych, w przeciwnym przypadku listę odrzuconych referatów wywołującego ją uczestnika 
 [talk] [speakerlogin] [start_timestamp] [title]

<b>(O) proposals [login] [password] </b> 
 zwraca listę propozycji referatów spontanicznych do zatwierdzenia lub odrzucenia, 
 zatwierdzenie lub odrzucenie referatu polega na wywołaniu przez organizatora funkcji 
 talk lub reject z odpowiednimi parametrami
 [talk] [speakerlogin] [start_timestamp] [title]

<b>(U) friends_talks [login] [password] [start_timestamp] [end_timestamp] [limit]</b> 
 lista referatów  rozpoczynających się w podanym przedziale czasowym wygłaszanych przez znajomych 
 danego uczestnika   posortowana wg czasu rozpoczęcia, wypisuje pierwsze <limit> referatów, przy 
 czym 0 oznacza, że należy wypisać wszystkie
 [talk] [speakerlogin] [start_timestamp] [title] [room]

<b>(U) friends_events [login] [password] [eventname]</b> 
 lista znajomych uczestniczących w danym wydarzeniu
 [login] [eventname] [friendlogin] 

<b>(U) recommended_talks [login] [password] [start_timestamp] [end_timestamp] [limit] </b>
 zwraca referaty rozpoczynające się w podanym przedziale czasowym, które mogą zainteresować danego 
 uczestnika (zaproponuj parametr <score> obliczany na podstawie dostępnych danych – ocen, 
 obecności, znajomości itp.), wypisuje pierwsze <limit> referatów wg nalepszego <score>, przy 
 czym 0 oznacza, że należy wypisać wszystkie
  [talk] [speakerlogin] [start_timestamp] [title] [room] [score
</pre>


