package org.ringingmaster.ui.desktop.methodrenderer.methodstyle;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Bell;

import java.util.Optional;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public interface MethodStyle {

    int FROM_TENOR_BELL_STYLE_MAX = 4;


//general
// TODO From C++   CString _name;

    //layout
    int getLeadHorizontalSpacing();
// TODO From C++   int _top;
    int getBellHorizontalSpacing();
    int getRowVerticalSpacing();
    Optional<Integer> getLeadsPerColumn();

//grid
// TODO From C++   int		 _gridWidthH;
// TODO From C++   int		 _gridWidthV;
// TODO From C++   COLORREF _gridColorH;
// TODO From C++   COLORREF _gridColorV;

//title
// TODO From C++   BOOL	_titleShow;
// TODO From C++   FontInfo _titleFont;

//numbers
// TODO From C++   FontInfo _numbersFont;

//starts
// TODO From C++   BOOL	 _startsShow;
// TODO From C++   FontInfo _startsFont;
// TODO From C++   BOOL	 _startsBlobs;

//notation
// TODO From C++   BOOL	 _notationShow;
// TODO From C++   FontInfo _notationFont;

//false leads
// TODO From C++   COLORREF _falseRowsColor;

//false leads
// TODO From C++   COLORREF _musicRowsColor;

//stroke
// TODO From C++   BOOL	 _handStrokeShow;
// TODO From C++   BOOL	 _backStrokeShow;
// TODO From C++   FontInfo _strokeFont;

//in course
// TODO From C++   BOOL	 _showInCourse;

//comments
// TODO From C++   BOOL	 _commentsShow;
// TODO From C++   FontInfo _commentsFont;


    BellStyle getFromTrebleBellStyle(int indexFromTreble);
    BellStyle getFromTenorBellStyle(int indexFromTenor);
    BellStyle getBlendedBellStyle(Bell bell, NumberOfBells numberOfBells);


//minus check boxes -- position calculated from tenor.
// TODO From C++   BOOL     _useMinus[MINUS_LINES_MAX];


// TODO From C++    StyleLine _lines[MAXBELLS];

// TODO From C++    StyleLine _minusLines[MINUS_LINES_MAX];
}
