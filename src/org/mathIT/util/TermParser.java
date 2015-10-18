/*
 * TermParser.java
 *
 * Copyright (C) 2010-2012 Andreas de Vries
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301  USA
 */
package org.mathIT.util;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Stack;
import static org.mathIT.numbers.BigNumbers.*;
/**
 *  This class provides methods to parse and evaluate strings representing
 *  computable terms.
 *  The infix operators +, -, *, /, %, ^, and mod
 *  are understood, as well as logical operators such as ==, &lt;=, &gt;=, "and", "or",
 *  "xor", &amp;&amp;, |, but <i>not</i> ||
 *  (note that &amp;, | are interpreted as logical operators, not as binary ones,
 *  and that = is equivalent to ==).
 *  The usual mathematical functions are also interpreted.
 *  A complete list of possible operators are determined in the array
 *  {@link #operator} and can be displayed by implementing the following snippet:
 *  <pre>
 *     String[] text = {
 *      "Variables","Functions", "Binary Infix Operators","Ternary Operators"
 *     };
 *     for (int i = 0; i &lt; TermParser.operator.length; i++) {
 *        System.out.println(text[i] + ":");
 *        for (int j = 0; j &lt; TermParser.operator[i].length; j++) {
 *           System.out.print(TermParser.operator[i][j] + ", ");
 *        }
 *        System.out.println("\n");
 *     }
 *  </pre>
 *  Fractional numbers (i.e., <code>double</code> values) have to be 
 *  inputted with a decimal point (not a comma).
 *  @author  Andreas de Vries
 *  @version 1.3
 */
public class TermParser implements java.io.Serializable {
   private static final long serialVersionUID = -42580021; // = "TermParser".hashCode()
   /** Names of predefined constants. */
   public static final String[] constantName  = {"PI", "pi", "E", "e"};
   /** Values of the predefined constants. */
   public static final double[] constantValue = {
      java.lang.Math.PI , java.lang.Math.PI, java.lang.Math.E,java.lang.Math.E
   };
   /** Table of predefined operators. In the <i>i</i>-throw row there are <i>i</i>-nary
    *  operators, variables are counted as nullary operators.
    *  Currently, the array is implemented as follows:
    *  <pre>
   public static final String[][] operator = {
      {// unary operators:
         "ln", "ld",
         "exp", "log",
         "sqrt", "w",
         "sin", "cos", "tan", "cot", "sec", "csc",
         "asin", "acos", "atan", "acot",
         "sinh", "cosh", "tanh", "coth",
         "arsinh", "arcosh", "artanh", "arcoth",
         "brown"
         <!-- "Z" -->
      },
      {// binary operators:
         "+", "-", "*", "/", "%", "^", "mod",
         "=", "&lt;", "&gt;", "==", "&lt;=", "&gt;=",
         "&amp;&amp;", "|", "&amp;", "and", "or", "xor",
         ";", ","
      },
      {// ternary operators:
         "if", "modPow"
      },
   };
    * </pre>
    */
   public static final String[][] operator = {
      {// variables:
         "x", "y", "z"
      },
      {// unary operators:
         "ln", "ld",
         "exp", "log",
         "sqrt", "w",
         "sin", "cos", "tan", "cot", "sec", "csc",
         "asin", "acos", "atan", "acot",
         "sinh", "cosh", "tanh", "coth", 
         "arsinh", "arcosh", "artanh", "arcoth",
         "brown" //, "Z"
      },
      {// binary operators:
         "+", "-", "*", "/", "%", "^", "mod",
         "=", "<", ">", "==", "<=", ">=",
         "&&", "|", "&", "and", "or", "xor",
         ";", ","
      },
      {// ternary operators:
         "if", "modPow"
      },
      /*
      {// quaternary operators:
         "for", "sum"
      }
      */
   };
   /** Maximum number of letters an operator can have. */
   public static int maxOpLength = 6;
   
   /** The formula in postfix notation. */
   private String[] formula;
      
   /** Creates a term parser from a single formula in usual notation,
    *  infix for binary operators and prefix for functions and ternary operators.
    *  The usual mathematical functions are also interpreted.
    *  A complete list of possible operators are determined in the array
    *  {@link #operator} and can be displayed by implementing the following snippet:
    *  <pre>
    *     String[] text = {
    *      "Variables","Functions", "Binary Infix Operators","Ternary Operators"
    *     };
    *     for (int i = 0; i &lt; TermParser.operator.length; i++) {
    *        System.out.println(text[i] + ":");
    *        for (int j = 0; j &lt; TermParser.operator[i].length; j++) {
    *           System.out.print(TermParser.operator[i][j] + ", ");
    *        }
    *        System.out.println("\n");
    *     }
    *  </pre>
    *  Fractional numbers (i.e., <code>double</code> values) have to be 
    *  inputted with a decimal point (not a comma).
    *  @param formula a string of the function in usual notation
    */
   public TermParser(String formula) {
      this.formula = parseToPostFix(formula);
   }
   
   /**
    * Analyses and parses the function being input as a string.
    * First the string is divided in its operators and operands,
    * then its syntax is checked. Only operators, integers or real numbers
    * are admissible.
    * @param formula a string representing a function
    * @return true if and only if the function could be parsed successfully
    */
   public static boolean checkSyntax(String formula) {
      return (null != analyse(formula));
   }
   
   /**
    * analyses and parses the function being input as a string.
    * First the string is divided in its operators and operands.
    * Then its syntax is checked. Only operators, integers or real numbers
    * are admissible. 
    */
   private static String[] analyse(String function) {
      // replace all constant names by their respective values:
      for ( int i = 0; i < constantName.length; i++ ) {
         function = function.replaceAll( constantName[i], Double.toString(constantValue[i] ) );
      }

      function = function.replaceAll(", ", ";");
      function = function.replaceAll(" ", "");
      function = function.replace(',', '.'); //??!!
      //function = function.replaceAll(";", ",");
      function = function.replace(':', '/');
      function = function.replace('[', '(');
      function = function.replace(']', ')');
      function = function.replace('{', '(');
      function = function.replace('}', ')');
      //function = function.replaceAll("||", "|");
      function = function.replaceAll("&&", "&");

      ArrayList<String> element = new ArrayList<>();
      int j = 0;

      int i = 0;
      while ( i < function.length() ) {
        String symbol = function.substring(i, i+1);
        if ( isDigit( symbol ) ) {
           int end = i;
           for (int k = i+1; k < function.length(); k++) {
              symbol = function.substring(k, k+1);
              if ( isDigit( symbol ) ) {
                 end++;
              } else 
                 break;
           } // for-loop

           if (i < end) {
              element.add( function.substring(i, end+1) );
           } else {
              element.add( function.substring(i, i+1) );
           }
        } else if ( symbol.equals("(") || symbol.equals(")" ) ) {
           element.add(symbol);
        } else {
           int opLength = 1;
           boolean isOp = false;
           while ( 
             !isOp && 
             (i + opLength <= function.length() ) && 
             opLength <= maxOpLength 
           ) {
              symbol = function.substring( i, i + opLength );
              isOp = isOperator( symbol );
              opLength++;
              if ( isOp && ( i + opLength <= function.length() ) ) {
                 String symbol2 = function.substring( i, i + opLength );
                 if ( isOperator( symbol2 ) ) { // is "sin" or "sinh" ...?
                    symbol = symbol2;
                 }
              }
           }
           if ( isOp ) {
              element.add( symbol );
           } else {
              return null; // syntax error: neither digit nor operator! 
           }
        }
        // "-" may be a prefix operator, but only after "(", boolean operators, 
        // comparison operators, or as the first element. Other constellations
        // cause an error.
        if ( j > 0 && ( element.get(j)).equals("-") ) {
          String secondLast = element.get(j-1);
          if ( 
            secondLast.equals("(")   ||
            secondLast.equals("=")   || secondLast.equals("==") ||
            secondLast.equals("<")   || secondLast.equals("<=") ||
            secondLast.equals(">")   || secondLast.equals(">=") ||
            secondLast.equals("&")   || secondLast.equals("&&") ||
            secondLast.equals("|")             ||
            secondLast.equalsIgnoreCase("and") || 
            secondLast.equalsIgnoreCase("or")  ||
            secondLast.equalsIgnoreCase("xor")
          ) {
             //element.add(j+1, element.get(j) );
             element.add(j, "0" );
             j++;
          }
        } else if ( j == 0 && ( element.get(j) ).equals("-") ) {
           element.add(0, "0" );
           j++;
        }
        // a^x mod n must be transformed to modPow(a,x,n):
        if ( j > 3 && element.get(j-1).equals("mod") && element.get(j-3).equals("^") ) {
        /* ---
        if ( j > 3 && element.get(j-1).equals("mod") ) {
           // Find index of the next operand:
           int index = j - 1;
           int i2 = index;
           boolean firstArgument = true;
           // 1st argument an operator?
           while (i2 > 0 && firstArgument) {
              index -= getArity(element.get(i2));
              firstArgument = (getArity(element.get(i2)) > 0 ) ? true : false;
              i2--;
              System.out.println("+++++ i2="+ i2 + ", element[i2]=" + element.get(i2) + " " + firstArgument);
           }
           if (element.get(index).equals("^")) {
        // ------  */
           String[] tmpOps = new String[6];
           tmpOps[0] = "modPow";
           tmpOps[1] = "(";
           tmpOps[2] = element.get(j-4);
           tmpOps[3] = element.get(j-2);
           tmpOps[4] = element.get(j);
           tmpOps[5] = ")";
           
           for ( int k = 0; k < 5; k++ ) {
              element.remove(j-k);
           }
           
           for ( int k = 0; k < 6; k++ ) {
              element.add(j - 4 + k,  tmpOps[k] );
           }
           j++;
           i += tmpOps[4].length() - 1;
        }
        i += element.get(j).length();
        j++;
     } // for-i

     String[] parsedFunction = new String[ element.size() ];
     parsedFunction = element.toArray( parsedFunction );
     return parsedFunction;
   }

   private String[] parseToPostFix(String function) {
      Stack<String> stack = new Stack<>();
      String[] elements;
      
      //embrace functions by brackets, such that the stack is not empty:
      function += ")";      
      stack.push("(");
      
      elements = analyse( function );

      if (elements == null) return null;

      String[] postFix = new String[elements.length];

      int i = 0;
      int j = 0;

      while ( !stack.isEmpty() ) {
         if ( isNumber(elements[i]) ) {
            postFix[j] = elements[i];
            j++;
         } else if (elements[i].equals("(")) {
            stack.push(elements[i]);
         } else if ( isOperator(elements[i]) ) {
            while (
              isOperator(stack.peek()) &&
              priority( stack.peek(), elements[i] )
            ) {
              String element = stack.pop();
              if ( !element.equals(",") && !element.equals(";") ) {
                 postFix[j] = element;
                 j++;
              }
            }
            stack.push(elements[i]);
         } else if ( elements[i].equals(")") ) {
            while (
              isOperator(stack.peek()) &&
              !(stack.peek()).equals("(")
           ) {
              String element = stack.pop();
              if ( !element.equals(",") && !element.equals(";") ) {
                 postFix[j] = element;
                 j++;
              }
           } 
           if ( (stack.peek()).equals("(") ) stack.pop();
        }
        i++;
     } // end-while

     int length = 0;

     for (int k = 0; k < postFix.length && postFix[k] != null; k++)
        length++;

     String postFix2[] = new String[length];
     System.arraycopy(postFix, 0, postFix2, 0, length);
     return postFix2;
  }

   /** Tests whether the argument <code>string</code> is a number or 
    *  a variable of the operator list.
    *  @param num a string representing a number, a variable, or an operator
    *  @return false if num is neither a number nor a variable
    */
   private static boolean isNumber(String num) {
      for ( int i = 0; i < operator[0].length; i++ ) {
         if ( num.equals( operator[0][i] ) ) {
            return true;
         }
      }
     
      boolean result;
      try {
         Double.parseDouble(num);
         result = true;
      } catch (NumberFormatException e) {
         result = false;
      }
      return result;
   }

   /** Tests whether the argument <code>string</code> is in the operator list. */
   private static boolean isOperator(String string) {
      boolean isOp = false;
      int i = 0;
      while ( !isOp && i < operator.length ) {
         isOp = isOperator( i, string );
         i++;
      }
      return isOp;
   }

   /** Tests whether the argument <code>string</code> is an i-nary operator.
    *  i=0 is a variable, i=1 a unary operator, i=2 a binary operator, i=3 a ternary operator, etc.
    */
   private static boolean isOperator( int i, String string ) {
      boolean isOp = false;
      int j = 0;
      while ( !isOp && j < operator[i].length ) {
         isOp = string.equalsIgnoreCase( operator[i][j] );
         j++;
      }
      return isOp;
   }
   
   /** Returns the arity of the operator. The arity is the number of arguments
    *  which an operator expects. The method returns 0 if the operator is a
    *  number or a variable, 1 if it is unary (i.e., a function),
    *  2 if it is binary, 3 if it is ternary, ...
    *  @param name the name of an operator
    *  @return the arity of the operator
    */
   private static int getArity(String name) {
      for (int i = operator.length - 1; i >= 0; i--) {
         for (int j = 0; j < operator[i].length; j++) {
            if (name.equalsIgnoreCase( operator[i][j] ) ) return i;
         }
      }
      return 0;  // <- arity of a constant number
   }
   
   private static boolean isDigit( String symbol ) {
      return (
         symbol.equals("1") || symbol.equals("2") || symbol.equals("3") ||
         symbol.equals("4") || symbol.equals("5") || symbol.equals("6") ||
         symbol.equals("7") || symbol.equals("8") || symbol.equals("9") ||
         symbol.equals("0") || symbol.equals(".")
      );
   }

   /**
    * checks if operator op1 has the same or a higher priority
    * than operator op2.
    */
   private boolean priority(String op1, String op2) {
      if ( ! isOperator( op1 ) ) return false;
      if ( ! isOperator( op2 ) ) return false;
     
      String[] op = {op1,op2};
      byte[] prioOP = new byte[2];

      for ( byte i = 0; i < op.length; i++ ) {
         if ( op[i].equals(",") || op[i].equals(";") )
            prioOP[i] = 0;
         // Boolean operators: -----------------------------
         else if ( op[i].equalsIgnoreCase("or") || op[i].equals("|") )
            prioOP[i] = 1;
         else if ( op[i].equals("&&") || op[i].equals("&") || op[i].equalsIgnoreCase("and") )
            prioOP[i] = 2;
         else if ( op[i].equalsIgnoreCase("xor") )
            prioOP[i] = 3;
         //-------------------------------------------------
         else if ( op[i].equals("mod") )
            prioOP[i] = 4;
         else if ( op[i].equals("=") || op[i].equals("==") )
            prioOP[i] = 5;
         else if ( op[i].equals("<") || op[i].equals(">") || op[i].equals("<=") || op[i].equals(">=") )
            prioOP[i] = 6;
         else if ( op[i].equals("+") || op[i].equals("-") )
            prioOP[i] = 7;
         else if ( op[i].equals("*") || op[i].equals("/") || op[i].equals("%") )
            prioOP[i] = 8;
         else if ( op[i].equals("^") )
            prioOP[i] = 9;
         else
            prioOP[i] =10;
      }
      return ( prioOP[0] >= prioOP[1] );
   }
   
   /** Returns a string of the formula, either in postfix or in
    *  usual notation. "Usual notation" here means,
    *  that the string contains infix notation for binary operators,
    *  such as <code>((2*x) + 2)</code>, and prefix notation for functions and 
    *  ternary operators, e.g., <code>sin(x)</code> or 
    *  <code>if(-1 &lt; x &amp;&amp; x &lt; 1; 1; 0)</code>.
    *  @param postfix specifies whether the returned string is in postfix notation
    *  @return a string representing the function, either in usual (infix/prefix) 
    *  or in postfix notation
    */
   public String getFormula(boolean postfix) {
      if (postfix) {
         String output = "";
         for (int j = 0; j < formula.length; j++) {
            output += formula[j] + " ";
         }
         return output;
      } else {
         return postfix2String(formula);
      }
   }
   
   /** Returns the specified function in postfix notation up to the last index
    *  into a usual (infix for binary operators, else prefix) string.
    *  @param postfix the function in postfix notation
    *  @return the function in prefix/infix notation
    */
   private String postfix2String(String[] postfix) {
      if ( postfix.length == 1 ) return postfix[0];
      
      String name = postfix[postfix.length - 1];
      int arity = getArity(name);
      
      if (arity <= 0) { // name represents a number or a variable
         String[] part = new String[postfix.length - 1];
         System.arraycopy(postfix, 0, part, 0, part.length);
         return postfix2String(part) + "," + name;
      } else if (arity == 1) { // name represents a unary function
         String[] part = new String[postfix.length - 1];
         System.arraycopy(postfix, 0, part, 0, part.length);
         return name + "(" + postfix2String(part) + ")";
      } else if (arity == 2) { // name represents a binary operator
         String[] part1;
         String[] part2;
         
         if ( isNumber(postfix[postfix.length - 2]) ) {
            part1 = new String[]{postfix[postfix.length - 2]};
            part2 = new String[postfix.length - 2];
            System.arraycopy(postfix, 0, part2, 0, part2.length);
         } else { // first argument is an operator
            // Find index of last operand:
            int index = postfix.length - 2;
            int i = index;
            boolean firstArgument = true;
            // 1st argument an operator?
            while (i > 0 && firstArgument) {
               index -= getArity(postfix[i]);
               firstArgument = (getArity(postfix[i]) > 0 ) ? true : false;
               i--;
               //System.out.println("+++++ i="+ i + ", postfix[i]=" + postfix[i] + " " + firstArgument);
            }
            
            boolean secondArgument = true;
            // 2nd argument an operator?
            while (i > 0 && secondArgument) {
               index -= getArity(postfix[i]);
               secondArgument = (getArity(postfix[i]) > 0 ) ? true : false;
               i--;
               //System.out.println("+++++ i="+ i + ", postfix[i]=" + postfix[i] + " " + firstArgument);
            }
            //System.out.println("+++ name="+ name + ", index = " + index);
            //int index = postfix.length - getArity(postfix[postfix.length - 2]) - 2;
            part1 = new String[postfix.length - index - 1];
            System.arraycopy(postfix, index, part1, 0, part1.length);
            part2 = new String[postfix.length - part1.length - 1];
            System.arraycopy(postfix, 0, part2, 0, part2.length);
            
            /* //
            System.out.print("### part1 = [");
            for(String s : part1) {
               System.out.print(s+", ");
            }
            System.out.println("]");
            System.out.print("### part2 = [");
            for(String s : part2) {
               System.out.print(s+", ");
            }
            System.out.println("]");
            
            System.out.println("+++ part1: ["+ index + ", " + (index + part1.length - 1) + "]");
            System.out.println("+++ part2: ["+ 0 + ", " + (part2.length - 1) + "]");
            // */
         }
         // use infix notation:
         return "("+postfix2String(part2)+" "+name+" "+postfix2String(part1)+")";
      } else if (name.equals("modPow")) { // modPow is to be displayed infix
         String[] part1, part2, part3;
         // Find index of last operand:
         int index = postfix.length - 2;
         int i = index;
         boolean firstArgument = true;
         // 1st argument an operator?
         while (i > 0 && firstArgument) {
            index -= getArity(postfix[i]);
            firstArgument = (getArity(postfix[i]) > 0 ) ? true : false;
            i--;
            //System.out.println("+++ 1) i="+ i + ", postfix[i]=" + postfix[i] + " " + firstArgument);
         }
         
         boolean secondArgument = true;
         // 2nd argument an operator?
         while (i > 0 && secondArgument) {
            index -= getArity(postfix[i]);
            secondArgument = (getArity(postfix[i]) > 0 ) ? true : false;
            i--;
            //System.out.println("+++ 2) i="+ i + ", postfix[i]=" + postfix[i] + " " + secondArgument);
         }
         
         part1 = new String[postfix.length - index - 1];
         System.arraycopy(postfix, index, part1, 0, part1.length);
         
         boolean thirdArgument = true;
         // 3rd argument an operator?
         while (i > 0 && thirdArgument) {
            index -= getArity(postfix[i]);
            thirdArgument = (getArity(postfix[i]) > 0 ) ? true : false;
            i--;
            //System.out.println("+++ 3) i="+ i + ", postfix[i]=" + postfix[i] + " " + thirdArgument);
         }
         //System.out.println("+++ name="+ name + ", index = " + index);
         //int index = postfix.length - getArity(postfix[postfix.length - 2]) - 2;
         part2 = new String[postfix.length - index - 1];
         System.arraycopy(postfix, index-1, part2, 0, part2.length);
         part3 = new String[postfix.length - part1.length - part2.length - 1];
         System.arraycopy(postfix, 0, part3, 0, part3.length);
         
         // use infix notation:
         return postfix2String(part3)+"^"+postfix2String(part2)+" mod "+postfix2String(part1);
      } else {
         String[] part = new String[postfix.length - 1];
         System.arraycopy(postfix, 0, part, 0, part.length);
         return name + "(" + postfix2String(part) + ")";
      }
   }
   
   /** Evaluates the function (in postfix notation) at the value <i>x</i>.
    * @param formula the formula in postfix notation, each array element being a postfix component
    * @param mc the mathcontext determining the precision
    * @return the function value at <i>x</i>
    */
   public static BigDecimal evaluate(String formula, MathContext mc) {
      TermParser parser = new TermParser(formula);
      return evaluatePostFix(parser.formula, mc);
   }
   /** Evaluates the function (in postfix notation) at the value <i>x</i>.
    * @param formula the formula in postfix notation, each array element being a postfix component
    * @param x the double value to be inserted in the function
    * @return the function value at <i>x</i>
    */
   private static BigDecimal evaluatePostFix(String[] formula, MathContext mc) {
      if (formula == null || formula.length == 0) return null;

      String[] postfix = new String[formula.length];
      System.arraycopy(formula, 0, postfix, 0, formula.length);

      Stack<String> stack = new Stack<>();
      BigDecimal result = null;
      for (int i = 0; i < postfix.length; i++) {
         try {
            if ( isNumber(postfix[i]) ) {
               stack.push(postfix[i]);
            } else {
               int arguments = 0, k = 1;
               while ( k < operator.length && arguments == 0 ) {
                  if ( isOperator( k, postfix[i] ) ) {
                     arguments = k;
                     //break; // while-loop
                  }
                  k++;
               }
               BigDecimal[] x = new BigDecimal[arguments];
               for ( int j = 0; j < arguments; j++ ) {
                  x[j] = new BigDecimal(stack.pop());
               }
               stack.push(evaluate(x, postfix[i], mc));
            }
         } catch (Exception e) {
            System.out.println("Error: Operands of " + postfix[i] + " wrong!");
            e.printStackTrace();
         }
      }
      try {
         result = new BigDecimal(stack.pop());
      } catch ( Exception e ) {
         System.out.println("Error: Operand is no number or function is null!");
         e.printStackTrace();
      }
      return result;
   }

  /** 
   * evaluates the operation determined by the operation op
   * and the operands x[0] and x[1].
   * For all operators the operands are pushed
   * from the stack in reverse order (postfix!), e.g.:
   * <p style="text-align:center">
   *   (2,1,-) = 1 - 2 = -1
   * </p>
   */
  private static String evaluate(BigDecimal[] x, String op, MathContext mc) {
     BigDecimal y = ZERO_DOT;

     if (op.equals("+")) {
        y = x[1].add(x[0]);
     } else if (op.equals("-")) {
        y = x[1].subtract(x[0]);
     } else if (op.equals("*")) {
        y = x[1].multiply(x[0]);
     } else if (op.equals("/")) {
        if ( !x[0].equals(ZERO_DOT) && !x[1].equals(ZERO_DOT))
           y = x[1].divide(x[0], mc);
        else if (x[0].equals(ZERO_DOT) && x[1].equals(ZERO_DOT))
           y = ONE_DOT;
//        else if (x[1].signum() > 0)
//           y = Double.POSITIVE_INFINITY;
//        else
//           y = Double.NEGATIVE_INFINITY;
     } else if (op.equals("%")) {
        BigInteger m = x[0].toBigInteger();
        BigInteger n = x[1].toBigInteger();
        y = new BigDecimal(n.mod(m));
     } else if (op.equals("mod")) {
        BigInteger m = x[0].toBigInteger();
        BigInteger n = x[1].toBigInteger();
        y = new BigDecimal(n.mod(m));
     } else if (op.equals("^") || op.equals("pow")) {
        y = pow(x[1], x[0].intValue());
     } else if (op.equals("sin")) {
        y = sin(x[0]);
     } else if (op.equals("cos")) {
        y = cos(x[0]);
        /*
     } else if (op.equals("tan")) {
        y = tan(x[0]);
     } else if (op.equals("cot")) {
        y = 1/tan(x[0]);
     } else if (op.equals("sec")) {
        y = 1/cos(x[0]);
     } else if (op.equals("csc")) {
        y = 1/sin(x[0]);
     } else if (op.equals("asin")) {
        y = asin(x[0]);
     } else if (op.equals("acos")) {
        y = acos(x[0]);
     } else if (op.equals("atan")) {
        y = atan(x[0]);
     } else if (op.equals("acot")) {
        y = atan(1/x[0]);
     } else if (op.equals("sinh")) {
        y = ( exp(x[0]) - exp(-x[0]) ) / 2;
     } else if (op.equals("cosh")) {
        y = ( exp(x[0]) + exp(-x[0]) ) / 2;
     } else if (op.equals("tanh")) {
        y = ( exp(x[0]) - exp(-x[0]) ) / ( exp(x[0]) + exp(-x[0]) );
     } else if (op.equals("coth")) {
        y = ( exp(x[0]) + exp(-x[0]) ) / ( exp(x[0]) - exp(-x[0]) );
     } else if (op.equals("arsinh")) {
        y = log( x[0] + sqrt( x[0]*x[0] + 1 ) );
     } else if (op.equals("arcosh")) {
        y = log( x[0] + sqrt( x[0]*x[0] - 1 ) );
     } else if (op.equals("artanh")) {
        y = log( (1 + x[0]) / (1 - x[0]) ) / 2;
     } else if (op.equals("arcoth")) {
        y = log( (x[0] + 1) / (x[0] - 1) ) / 2;
         */
     } else if (op.equals("exp")) {
        y = exp(x[0]);
     } else if (op.equals("ln")) {
        y = ln(x[0]);
     } else if (op.equals("log")) {
        y = ln(x[0]).divide(ln(BigDecimal.valueOf(10)), mc);
     } else if (op.equals("ld")) {
        y = ln(x[0]).divide(ln(BigDecimal.valueOf(2)), mc);
        //y = log(x[0]) / log(2);
     } else if ( op.equals("w") || op.equals("sqrt") ) {
        y = sqrt(x[0],mc.getPrecision());
     } else if ( op.equalsIgnoreCase("brown") ) {
        y = brown( x[0].intValue(), mc.getPrecision());
        /*
     } else if ( op.equals("and") || op.equals("&&") || op.equals("&") ) {
        y = x[1] * x[0];
     } else if ( op.equals("or") || op.equals("|") ) {
        y = x[1] >= x[0] ? x[1] : x[0];
     } else if ( op.equals("xor") ) {
        y = (x[1] + x[0]) % 2;
     } else if ( op.equals("if") ) {
        y = x[2] == 1 ? x[1] : x[0];
         */
     } else if ( op.equals("modPow") ) {
        //y = modPow( round(x[2]), round(x[1]), round(x[0]) );
        y = new BigDecimal(modPow(
          x[2].toBigInteger(),x[1].toBigInteger(),x[0].toBigInteger()
        ));
        /*
     } else if (op.equals("<")) {
        if ( x[1] < x[0] )
           y = 1; // true
        else 
           y = 0; // false
     } else if (op.equals("<=")) {
        if ( x[1] <= x[0] )
           y = 1; // true
        else 
           y = 0; // false
     } else if (op.equals(">")) {
        if ( x[1] > x[0] )
           y = 1; // true
        else 
           y = 0; // false
     } else if (op.equals(">=")) {
        if ( x[1] <= x[0] )
           y = 1; // true
        else 
           y = 0; // false
     } else if (op.equals("==") || op.equals("=") ) {
        if ( x[1] == x[0] )
           y = 1; // true
        else 
           y = 0; // false
         */
     }
//System.out.println(" => " + erg);
     return y.toPlainString();
  }
  
  /** For test purposes...*/
  /*
  public static void main( String[] args ) {     
     double x = 3;
     if (args != null && args.length>0 )
        x = Double.parseDouble( args[0] );
     
     long startTime = System.currentTimeMillis();
     
     String[] fList = {"4^x mod 437", "11^x mod 1147", "(7+1)^(2*x+2) mod 23"};

     for ( int i = 0; i < fList.length; i++ ) {
        boolean ok = checkSyntax( fList[i] );
        System.out.println("Funktion " + i + " ok? " + ok );
     }
     System.out.println("Running time for checking: " + (System.currentTimeMillis() - startTime)/1000.0 + " sec" );
     startTime = System.currentTimeMillis();
     
     TermParser parser = new TermParser( fList );
     System.out.println("Running time for parsing: " + (System.currentTimeMillis() - startTime)/1000.0 + " sec" );
     
     String funktion;
     System.out.println("Postfix: -----");
     for (int k = 0; (funktion = parser.getFunction(k,true)) != null; k++) {
        System.out.println(funktion);
     }
     
     System.out.println("Infix: ---");
     for (int k = 0; (funktion = parser.getFunction(k,false)) != null; k++) {
        System.out.println(funktion);
     }
     
     startTime = System.currentTimeMillis();

     double y;
     //y = parser.evaluatePostFix( postfix, x );
     //System.out.println("y=" + y);
     
     for ( int i = 0; i < fList.length; i++ ) {
        //String[] postfix = parser.getFunction(i);
        //y = parser.evaluatePostFix( postfix, x );
        y = parser.evaluate( i, x );
        System.out.println("List: y=" + y);
     }
     System.out.println("Running time for evaluating: " + (System.currentTimeMillis() - startTime)/1000.0 + " sec" );

     startTime = System.currentTimeMillis();
     for ( int i = 0; i < fList.length; i++ ) {
        boolean ok = checkSyntax( fList[i] );
        System.out.println("Funktion " + i + " ok? " + ok );
     }
     System.out.println("Running time for checking: " + (System.currentTimeMillis() - startTime)/1000.0 + " sec" );
  }
  // */
}
