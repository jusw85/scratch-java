#!/usr/bin/env python

from itertools import combinations, chain


def powerset(iterable):
    s = list(iterable)
    return chain.from_iterable(combinations(s, r) for r in range(len(s) + 1))


def naive(text, width):
    words = text.split()
    count = len(words)

    minimum = 10 ** 20
    breaks = ()
    for b in powerset(range(1, count)):
        m = 0
        i = 0
        for j in chain(b, (count,)):
            w = len(' '.join(words[i:j]))
            if w > width:
                break
            m += (width - w) ** 2
            i = j
        else:
            if m < minimum:
                minimum = m
                breaks = b

    lines = []
    i = 0
    for j in chain(breaks, (count,)):
        lines.append(' '.join(words[i:j]))
        i = j
    return lines


def naive2(text, width):
    words = text.split()
    count = len(words)

    minimum = 10 ** 20
    breaks = ()
    for b in powerset(range(1, count)):
        m = []
        i = 0
        # pdb.set_trace()
        for j in chain(b, (count,)):
            line = ' '.join(words[i:j])
            w = len(line)
            numwords = abs(j - i)
            if w > width and numwords > 1:
                break
            if w <= width:
                m.append((width - w) ** 2)
            else:
                m.append(0)
            i = j
        else:
            total = sum(m[:-1])
            if total < minimum:
                minimum = total
                breaks = b

    lines = []
    i = 0
    for j in chain(breaks, (count,)):
        lines.append(' '.join(words[i:j]))
        i = j
    return lines


def dynamic(text, width):
    """
    The deficiency of first idea lies in that it repeatedly solves the same subproblems. Yet suppose there was an optimal configuration of lines. Plucking off its last line would still keep the layout optimal because otherwise it would be possible to improve it and, together with the removed line, would result in even better configuration, contradicting its optimality. To solve each subproblem just once, it is then necessary to find out and later re-use which of the lines ending with some word contributes least to the overall cost. As each of the "n" words could terminate at most "n" potential lines, the algorithm runs in O(n ^ 2).
    """
    words = text.split()
    count = len(words)
    slack = [[0] * count for i in range(count)]
    for i in range(count):
        slack[i][i] = width - len(words[i])
        for j in range(i + 1, count):
            slack[i][j] = slack[i][j - 1] - len(words[j]) - 1

    minima = [0] + [10 ** 20] * count
    breaks = [0] * count
    for j in range(count):
        i = j
        while i >= 0:
            if slack[i][j] < 0:
                cost = 10 ** 10
            else:
                cost = minima[i] + slack[i][j] ** 2
            if cost < minima[j + 1]:
                minima[j + 1] = cost
                breaks[j] = i
            i -= 1

    lines = []
    j = count
    while j > 0:
        i = breaks[j - 1]
        lines.append(' '.join(words[i:j]))
        j = i
    lines.reverse()
    return lines


def shortest(text, width):
    """
    The previous way can be sped up even further: the length offsets used to calculate any line length in constant time can easily be pre-processed in O(n), rather than O(n ^ 2), and there is no point in putting ever more words on a line once it reaches the allowed width. The performance then improves down to O(n * width).

    This is exactly the same result as if the text was thought of as a (topologically sorted) directed acyclic graph, with the nodes and arcs representing words and breaks, respectively. By substituting the penalties for the weights, the problem becomes the one of finding the shortest path which is known to be solvable in linear time. Note that the number of edges remains O(n * width).

    Flat uses the latter method.
    """
    words = text.split()
    count = len(words)
    offsets = [0]
    for w in words:
        offsets.append(offsets[-1] + len(w))

    minima = [0] + [10 ** 20] * count
    breaks = [0] * (count + 1)
    for i in range(count):
        for j in range(i + 1, count + 1):
            w = offsets[j] - offsets[i] + j - i - 1
            if w > width:
                break
            cost = minima[i] + (width - w) ** 2
            if cost < minima[j]:
                minima[j] = cost
                breaks[j] = i

    lines = []
    j = count
    while j > 0:
        i = breaks[j]
        lines.append(' '.join(words[i:j]))
        j = i
    lines.reverse()
    return lines


def shortest2(text, width):
    words = text.split()
    words.reverse()

    count = len(words)
    offsets = [0]

    for w in words:
        offsets.append(offsets[-1] + len(w))

    minima = [0] + [10 ** 20] * count
    breaks = [0] * (count + 1)
    for i in range(count):
        # pdb.set_trace()
        for j in range(i + 1, count + 1):
            # w = offsets[j] - offsets[i] + j - i - 1
            # if w > width:
            #     break
            # if i == 0:
            #     cost = 0
            # else:
            #     cost = minima[i] + (width - w) ** 2

            # numwords = abs(j - i)
            # if w > width and numwords > 1:
            #     break
            # if w <= width:
            #     cost = minima[i] + (width - w) ** 2
            # else:
            #     cost = 0

            numwords = j - i
            w = offsets[j] - offsets[i] + j - i - 1
            if w > width and numwords > 1:
                break
            if w <= width and i != 0:
                cost = minima[i] + (width - w) ** 2
            else:
                cost = 0

            if cost < minima[j]:
                minima[j] = cost
                breaks[j] = i

    # print(minima)
    # print(breaks)

    lines = []
    j = count
    while j > 0:
        i = breaks[j]
        line = ' '.join(words[i:j][::-1])
        lines.append(line)
        j = i

    # lines.reverse()
    return lines

# text = "a b c d e f g h i j k l m n o p qqqqqqqqq"
# print('\n'.join(naive2(text, 9)))
# print('\n'.join(shortest2(text, 9)))

# text = "q q q q qqqqqq"
# print('\n'.join(naive2(text, 6)))
# print('\n'.join(shortest2(text, 6)))
# print('\n'.join(naive2(text, 5)))
# print('\n'.join(shortest2(text, 5)))

# text = "aaa bb cc ddddd aa a a"
# print('\n'.join(naive2(text, 6)))
# print('\n'.join(shortest2(text, 6)))

# text = "aaa bb cc dddd"
# text = "aaaa b c"
# print('\n'.join(naive2(text, 6)))
# print('\n'.join(shortest2(text, 6)))

# text = "aaaaa a aaa"
# print('\n'.join(naive2(text, 4)))
# print('\n'.join(shortest2(text, 4)))
