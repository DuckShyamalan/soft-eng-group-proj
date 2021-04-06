CONTENT=$(shell ls content | sed 's/.md/.tex/;s/^/content-/')

report.pdf: report.tex $(CONTENT) 
	echo "" | pdflatex report
	#echo "" | biber report
	echo "" | pdflatex report
	echo "" | pdflatex report


content-%.tex: content/%.md
	echo "" | pandoc -t latex $< -o $@
