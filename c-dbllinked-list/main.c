#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "main.h"

/*
define a doubly linked list of heap-allocated strings. 
Write functions to insert, find, and delete items from it. Test them.
*/

typedef struct dlnode {
    struct dlnode *next, *prev;
    char *str;
} dlnode;

struct dlnode *create(const char *str);
int insert_after(struct dlnode* at, struct dlnode* node);
//int insert_before(struct dlnode* at, struct dlnode* node);
void print_list_forward(dlnode *start);
//void print_list_backward(dlnode *start);
int append(dlnode *list, dlnode *node);

dlnode* find_by_index(dlnode *list, int index);
dlnode* find_by_string(dlnode *list, const char *str);

int delete(dlnode *node);

int main(int argc, const char **argv) {
    printf("Hello Doubly linked list demo\nArgs:\n");
    for (int arg = 0; arg < argc; arg++)
        printf("%d: %s\n", arg, argv[arg]);

    dlnode* list = create("first node");
    append(list, create("second"));
    dlnode *third = create("third");
    append(list, third);
    append(list, create("fourth"));
    //printf("1>%s\n", list->str);

    print_list_forward(list);
    delete(third);
    print_list_forward(list);

    return 0; 
}

struct dlnode *create(const char *str) {
    struct dlnode *node = malloc(sizeof(struct dlnode));
    node->next = NULL;
    node->prev = NULL;
    node->str = malloc(sizeof(char) * strlen(str));
    strcpy(node->str, str);
    return node;
}

int insert_after(struct dlnode* at, struct dlnode* node) {
    if (at == NULL || node == NULL) 
        return -1;
    // at <-> next
    // ->
    // at <-> node <-> next
    
    dlnode *atnext = at->next, *next_prev = atnext ? atnext->prev : NULL;

    
    node->prev = at;
    node->next = atnext;

    if (atnext)
        atnext->prev = node;
    at->next = node;

    return 0;
}

void print_list_forward(dlnode *node) {
    for (int i = 0; node; node = node->next, i++)
        printf("[%d]: %s\n", i, node->str);
}

int append(dlnode *list, dlnode *node) {
    dlnode *end = list;
    while (end->next) end = end->next;
    return insert_after(end, node);
}

dlnode* find_by_index(dlnode *list, int index) {
    if (list == NULL)
        return NULL;
        
    if (index == 0)
        return list;
    
    if (index > 0)
        return find_by_index(list->next, index - 1);
    else // advance in other direction
        return find_by_index(list->prev, index + 1);
}

dlnode* find_by_string(dlnode *list, const char *str) {
    if (list == NULL)
        return NULL;
        
    if (strcmp(str, list->str) == 0)
        return list;
    
    return find_by_string(list->next, str);
}

int delete(dlnode *node) {
    if (!node)
        return -1;

    dlnode *prev=node->prev, *next=node->next;

    free(node->str);
    free(node);

    if (prev)
        prev->next = next;
    if (next)
        next->prev = prev;

    return 0;
}