(define (flat-even xss)
  (if (null? xss)
      '()
      (flat-even-rec (car xss) (cdr xss))))

(define (flat-even-rec yss zss)
	(if (null? yss)
          (flat-even zss)
                    (cons (car yss)
                (cons (car (cdr yss))
                      (flat-even (cons (cdr (cdr yss))
                                       zss))))))

(display (flat-even '((a b) (a b a b) () (a b))))
