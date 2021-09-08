type ide = string;;
type exp =
		Eint of int
	| Ebool of bool
	| Den of ide
	| Prod of exp * exp
	| Sum of exp * exp
	| Diff of exp * exp
	| Eq of exp * exp
	| Minus of exp
	| IsZero of exp
	| Or of exp * exp
	| And of exp * exp
	| Not of exp
	| Ifthenelse of exp * exp * exp
	| Let of ide * exp * exp
	| Fun of ide * exp
	| FunCall of exp * exp
	| Letrec of ide * exp * exp
	| FunDouble of ide * ide * exp
	| FunCallDouble of exp * exp * exp
	(*dizionario e operazioni associate*)
	| Edict of dict
	| Insert of ide * exp * exp
	| Delete of ide * exp
	| Has_key of ide * exp
	| Iterate of exp * exp
	| Fold of exp * exp
	| Filter of (ide list) * exp
	and dict = Empty | Val of ide * exp * dict;;


(*ambiente polimorfo*)
type 't env = ide -> 't;;
let emptyenv (v : 't) = function x -> v;;
let applyenv (r : 't env) (i : ide) = r i;;
let bind (r : 't env) (i : ide) (v : 't) = function x -> if x = i then v else applyenv r x;;


(*tipi esprimibili*)
type evT =
		Int of int
	| Bool of bool
	| Unbound
	| FunVal of evFun
	| FunValDouble of evFunDouble
	| RecFunVal of ide * evFun
	| DictVal of (ide * evT) list
and evFun = ide * exp * evT env
and evFunDouble = ide * ide * exp * evT env (*chiusura di una funzione con 2 argomenti*)


(*rts*)
(*type checking*)
let typecheck (s : string) (v : evT) : bool = match s with
	"int" -> (match v with
		Int(_) -> true
		|_ -> false)
	| "bool" -> (match v with
		Bool(_) -> true
		|_ -> false)
	| "unbound" -> (match v with
		Unbound -> true
	  |_ -> false)
	| "dict" -> (match v with
		DictVal(_) -> true
		|_ -> false)
	|_ -> failwith("not a valid type");;


(*funzioni primitive*)
let prod x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Int(n*u))
	else failwith("Type error");;

let sum x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Int(n+u))
	else failwith("Type error");;

let diff x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Int(n-u))
	else failwith("Type error");;

let eq x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Bool(n=u))
	else failwith("Type error");;

let minus x = if (typecheck "int" x)
	then (match x with
	   	Int(n) -> Int(-n))
	else failwith("Type error");;

let iszero x = if (typecheck "int" x)
	then (match x with
		Int(n) -> Bool(n=0))
	else failwith("Type error");;

let vel x y = if (typecheck "bool" x) && (typecheck "bool" y)
	then (match (x,y) with
		(Bool(b),Bool(e)) -> (Bool(b||e)))
	else failwith("Type error");;

let et x y = if (typecheck "bool" x) && (typecheck "bool" y)
	then (match (x,y) with
		(Bool(b),Bool(e)) -> Bool(b&&e))
	else failwith("Type error");;

let non x = if (typecheck "bool" x)
	then (match x with
		Bool(true) -> Bool(false) |
		Bool(false) -> Bool(true))
	else failwith("Type error");;


(*interprete*)
let rec eval (e : exp) (r : evT env) : evT = match e with
	Eint n -> Int n |
	Ebool b -> Bool b |
	IsZero a -> iszero (eval a r) |
	Den i -> applyenv r i |
	Eq(a, b) -> eq (eval a r) (eval b r) |
	Prod(a, b) -> prod (eval a r) (eval b r) |
	Sum(a, b) -> sum (eval a r) (eval b r) |
	Diff(a, b) -> diff (eval a r) (eval b r) |
	Minus a -> minus (eval a r) |
	And(a, b) -> et (eval a r) (eval b r) |
	Or(a, b) -> vel (eval a r) (eval b r) |
	Not a -> non (eval a r) |
	Ifthenelse(a, b, c) ->
		let g = (eval a r) in
			if (typecheck "bool" g)
				then (if g = Bool(true) then (eval b r) else (eval c r))
				else failwith ("nonboolean guard") |
	Let(i, e1, e2) -> eval e2 (bind r i (eval e1 r)) |
	Fun(i, a) -> FunVal(i, a, r) |
	FunCall(f, eArg) ->
		let fClosure = (eval f r) in
			(match fClosure with
				FunVal(arg, fBody, fDecEnv) ->
					eval fBody (bind fDecEnv arg (eval eArg r)) |
				RecFunVal(g, (arg, fBody, fDecEnv)) ->
					let aVal = (eval eArg r) in
						let rEnv = (bind fDecEnv g fClosure) in
							let aEnv = (bind rEnv arg aVal) in
								eval fBody aEnv |
				_ -> failwith("non functional value")) |
  Letrec(f, funDef, letBody) ->
      (match funDef with
          Fun(i, fBody) -> let r1 = (bind r f (RecFunVal(f, (i, fBody, r)))) in
                   			                eval letBody r1 |
      		_ -> failwith("non functional def")) |
	FunDouble(i1, i2, a) -> FunValDouble(i1, i2, a, r) |
	FunCallDouble(f, e1, e2) ->
		(match (eval f r) with
			FunValDouble(arg1, arg2, fBody, fDecEnv) ->
				(eval fBody (bind (bind fDecEnv arg2 (eval e2 r)) arg1 (eval e1 r)))
			| _ ->	failwith("Type error"))
	 |
	(*Dizionario *)
	Edict(d) -> DictVal(evalDict d r "undefined" []) |
	Has_key(i, d) -> (match eval d r with
	  DictVal(ls) -> has_key i ls
	  |_ -> failwith("expected dictionary")) |
	Insert(i, e, d) -> (match eval d r with
		DictVal(ls) ->  (match has_key i ls with
			Bool false -> let value = eval e r in
				if (typecheck (dict_type ls) value)
				then DictVal((i, value)::ls)
				else failwith("Type error")
			|_ -> failwith("key already present")
			)
		|_ -> failwith("Insert error")) |
	Delete(i, d) -> (match eval d r with
			DictVal(ls) -> DictVal(delete i ls)
			|_ -> failwith("Type error")) |
	Iterate(f, d) -> (match eval d r with
		DictVal(ls) -> DictVal(iterate (eval f r) ls r)
		|_ -> failwith("Type error")) |
	Filter(l, d) ->( match eval d r with
		DictVal(ls) -> DictVal(filter l ls)
		|_ -> failwith("Type error")) |
	Fold(f, d) ->  match eval d r with
		DictVal(ls) -> match (dict_type ls) with
				"int" ->  fold (eval f r) ls (Int(0)) r
				| "bool" -> fold (eval f r) ls (Bool(false)) r
				|_ ->  fold (eval f r) ls Unbound r
		|_ -> failwith("Type error")

	(* funzioni di supporto *)
	and evalDict (d : dict) (r : evT env) (tpe : string) (lst : (ide * evT) list) : (ide * evT) list =
		match d with
				Empty -> lst
			| Val(i, e, ls) -> (match (has_key i lst) with
          Bool false -> let value = (eval e r) in (
            match tpe with
			       		"undefined" -> (evalDict ls r (if (typecheck "int" value) then "int" else "bool") ((i, value)::lst))
			       	| "int" -> if(typecheck "int" value)
			       		then (evalDict ls r "int" ((i, value)::lst))
			       		else failwith("Type error")
			       	| "bool" -> if(typecheck "bool" value)
			       		then (evalDict ls r "bool" ((i, value)::lst))
			       		else failwith("Type error")
          )
        | _ -> failwith("Key already present"))
			| _-> failwith("Type error")

	and has_key (i : ide) (lst : (ide * evT) list) : evT =
    match lst with
      [] -> Bool false
      | (x,v)::xs -> if x = i then Bool true else has_key i xs

	and dict_type (d : (ide * evT) list) : (string) =
		match d with
			[] -> "undefined"
			| (i, e)::ls -> (if (typecheck "int" e) then "int" else "bool")

	and delete (id: ide) (d: (ide * evT) list) : ((ide * evT) list) =
		match d with
		[] -> []
		| (i, v)::ls -> if (i = id) then ls else (i, v)::(delete id ls)

	and iterate (f: evT)(d : (ide*evT) list) (r : evT env) : ((ide*evT) list) =
		match d with
		 [] -> []
		| (i, v):: ls -> (i, (funcall f v r))::(iterate f ls r)
		|_ -> failwith("Type error")

	and funcall (fClosure : evT) (v : evT) (r : evT env) : evT =
			(match fClosure with
				FunVal(arg, fBody, fDecEnv) ->
					eval fBody (bind fDecEnv arg v) |
				RecFunVal(g, (arg, fBody, fDecEnv)) ->
					let aVal = v in
						let rEnv = (bind fDecEnv g fClosure) in
							let aEnv = (bind rEnv arg aVal) in
								eval fBody aEnv |
				_ -> failwith("non functional value"))

	and filter (idl : ide list) (d : (ide * evT) list) : ((ide * evT) list) =
		match d with
		 [] -> []
		| (i,v)::xs -> if contains idl i then (i,v):: filter idl xs else filter idl xs

	and contains (l: ide list) (i : ide) :(bool) =
		match l with
			[] -> false
			|x::xs -> if x=i then true else contains xs i

	and fold (funct:evT) (d:(ide*evT) list) (acc:evT) (r :evT env)  : evT  = (match d with
		[] -> acc
		| (id,x)::xs -> (match (acc,x) with
			((Int(u)),(Int(w))) -> (fold funct xs (funcalldouble funct x acc r) r)
			| ((Bool(u)),(Bool(w))) -> (fold funct xs (funcalldouble funct x acc r) r)
			| _ -> failwith ("Invalid fold operation"))
		| _ -> failwith("Type error"))

	and funcalldouble (fClosure : evT) (u : evT) (v : evT) (r : evT env) : evT =
			(match fClosure with
					FunValDouble(arg1, arg2, fBody, fDecEnv) ->
						(eval fBody (bind (bind fDecEnv arg2 v) arg1 u ))
				| _ ->	failwith("Type error"))

;;



(* -------------------------BATTERIA DI TEST----------------------------------*)


let env0 = emptyenv Unbound;;
let f = Fun("y", Sum(Den "y", Eint 1));;
let fd = FunDouble("acc","y",Sum(Den "acc",Sum(Den "y", Eint 5)));;
let fdb = FunDouble("acc","y",Or(Den "acc",Or(Den "y", Ebool false)));;

(*----------------------------TEST DIZIONARIO---------------------------------*)

(*--------------Creazione di un dizionario di interi--------------------------*)
let e1 = Edict(Val("a", Eint(10), Val("b", Eint(5), Val("c", Eint(15), Empty))));;

eval e1 env0;;

(*----------------Creazione di un dizionario di booleani----------------------*)
let e2 = Edict(Val("a", Ebool(true), Val("b", Ebool(false), Val("c", Ebool(true), Empty))));;

eval e2 env0;;

(*--Creazione di un dizionario contenente tipi non omogenei: Genera un errore--*)
(*let e3 = Edict(Val("a", Ebool(true), Val("b", Ebool(false), Val("c", Eint(3), Empty))));;

eval e3 env0;;*)


(*-------------------------------TEST INSERT----------------------------------*)

(*Insert di una coppia chiave, valore intero in un dizionario di interi(e1)*)
let e4 = Insert( "d", Eint(23), e1);;

eval e4 env0;;

(*Insert di una coppia chiave, valore booleano in un dizionario di booleani(e2)*)

let e5 = Insert( "d", Ebool(true), e2);;

eval e5 env0;;

(*Insert di una coppia chiave, valore intero in un dizionario di booleani(e2):
	Genera un errore.*)

(*let e6 = Insert( "e", Eint(23), e2);;

eval e6 env0;;*)

(*Insert di una coppia chiave, valore intero (gia' presente)
	in un dizionario di interi(e1): Genera un errore("Key already present")*)
(*	let e9 = Insert( "a", Eint(23), e1);;

	eval e9 env0;;*)


(*------------------------------TEST HAS_KEY----------------------------------*)

(*Controlla se il dizionario e1 contiene la chiave "a": Deve ritornare "true"*)
let e6 = Has_key("a", e1);;

eval e6 env0;;

(*Controlla se il dizionario e1 contiene la chiave "h": Deve ritornare "false"*)
let e7 = Has_key("h", e1);;

eval e7 env0;;

(*Controlla se il dizionario e2 contiene la chiave "a": Deve ritornare "true"
	(test per i booleani)*)

let e8 = Has_key("a", e2);;

eval e8 env0;;


(*------------------------------TEST DELETE----------------------------------*)

(*Elimina l'elemento corrispondente alla chiave "a" nel dizionario e1*)
let e10 = Delete("a", e1);;

eval e10 env0;;


(*------------------------------TEST ITERATE----------------------------------*)

(*Utilizzo Iterate per incrementare ogni elemento del dizionario e1 di uno*)
let e11 = Iterate(f, e1);;

eval e11 env0;;

(*Utilizzo Iterate per incrementare ogni elemento del dizionario e2 di uno:
 	Genera un errore*)
(*let e12 = Iterate(Fun("y", Sum(Den "y", Eint 1)), e2);;

eval e12 env0;;*)

(*Utilizzo Iterate per effettuare l'OR logico tra "true" e gli elementi di e2*)
let e13 = Iterate(Fun("y", Or(Den "y", Ebool(true))), e2);;

eval e13 env0;;


(*-------------------------------TEST FILTER----------------------------------*)

(*Elimino dal dizionario e1 tutti gli elementi non corrispondenti alle chiavi
 	"a","c" e "d"*)
let e14 = Filter(["a";"c";"h"], e1);;

eval e14 env0;;


(*-------------------------------TEST FOLD----------------------------------*)

(*Utilizzo la FOLD con la funzione "fd" che incrementa il valore di ogni
	elemento del dizionario e lo somma ad un accumulatore iniziale di tipo
	intero(deve restituire 0 + (5 + 5) + (10 + 5) + (15 + 5) = 45)*)
let e15 = Fold(fd,e1);;

eval e15 env0;;

(*Utilizzo la FOLD con la funzione "fd" su un dizionario di booleani:
	Genera un errore*)

(*let e16 = Fold(fd,e2);;

eval e16 env0;;*)


(*Utilizzo la FOLD con la funzione "fdb" che restituisce il valore di
	un'operazione Or tra tutti gli elementi del dizionario e l'accumulatore
	iniziale posto a false
	(F v (T v F) v (F v F) v (T v F) = T) *)
let e17 = Fold(fdb,e2);;

eval e17 env0;;

(*Utilizzo la FOLD su un dizionario vuoto restituendo l'accumulatore "Unbound"*)
let e18 = Fold(fdb, Edict(Empty));;

eval e18 env0;;
